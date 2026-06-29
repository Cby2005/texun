import os
from io import BytesIO
from pathlib import Path
from threading import Lock

import torch
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from PIL import Image, ImageOps, UnidentifiedImageError
from ultralytics import YOLO

ROOT = Path(__file__).resolve().parents[1]
MODEL_PATH = Path(os.getenv("YOLO_MODEL_PATH", ROOT / "model" / "best.pt"))
DEVICE = os.getenv("YOLO_DEVICE") or ("0" if torch.cuda.is_available() else "cpu")
DEFAULT_CONFIDENCE = float(os.getenv("YOLO_CONFIDENCE", "0.5"))
MAX_FILE_SIZE = 5 * 1024 * 1024
ALLOWED_TYPES = {"image/jpeg", "image/png", "image/webp"}

if not MODEL_PATH.is_file():
    raise RuntimeError(f"Model not found: {MODEL_PATH}")

model = YOLO(str(MODEL_PATH))
model_lock = Lock()
app = FastAPI(title="Strawberry Disease YOLO Service", version="1.0.0")


@app.get("/health")
def health():
    return {
        "status": "ok",
        "model": MODEL_PATH.name,
        "task": model.task,
        "device": torch.cuda.get_device_name(0) if DEVICE != "cpu" else "cpu",
        "classes": model.names,
    }


@app.post("/detect")
async def detect(file: UploadFile = File(...), conf: float = Form(DEFAULT_CONFIDENCE)):
    if file.content_type not in ALLOWED_TYPES:
        raise HTTPException(status_code=415, detail="Only jpg, png and webp images are supported")
    if not 0 <= conf <= 1:
        raise HTTPException(status_code=422, detail="conf must be between 0 and 1")

    content = await file.read(MAX_FILE_SIZE + 1)
    if len(content) > MAX_FILE_SIZE:
        raise HTTPException(status_code=413, detail="Image must not exceed 5MB")

    try:
        image = ImageOps.exif_transpose(Image.open(BytesIO(content))).convert("RGB")
    except (UnidentifiedImageError, OSError) as error:
        raise HTTPException(status_code=400, detail="Invalid image file") from error

    with model_lock:
        result = model.predict(image, conf=conf, imgsz=640, device=DEVICE, verbose=False)[0]

    predictions = []
    if result.boxes is not None:
        for box in result.boxes:
            class_id = int(box.cls.item())
            x, y, width, height = box.xywh[0].tolist()
            predictions.append({
                "className": result.names[class_id],
                "confidence": round(float(box.conf.item()), 6),
                "x": round(x),
                "y": round(y),
                "width": round(width),
                "height": round(height),
            })

    predictions.sort(key=lambda item: item["confidence"], reverse=True)
    top = predictions[0] if predictions else None
    return {
        "success": True,
        "message": "识别成功" if top else "未识别到置信度达到阈值的目标",
        "modelId": MODEL_PATH.name,
        "topDiseaseName": top["className"] if top else None,
        "topConfidence": top["confidence"] if top else None,
        "provider": "local",
        "predictions": predictions,
    }
