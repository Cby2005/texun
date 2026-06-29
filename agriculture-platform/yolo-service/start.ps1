$ErrorActionPreference = 'Stop'

$python = Join-Path $PSScriptRoot '.venv/Scripts/python.exe'
if (-not (Test-Path $python)) {
    throw 'YOLO environment is missing. Run yolo-service/setup.ps1 first.'
}

$env:YOLO_MODEL_PATH = Join-Path (Split-Path $PSScriptRoot -Parent) 'model/best.pt'
$env:YOLO_CONFIDENCE = '0.5'
& $python -m uvicorn app:app --app-dir $PSScriptRoot --host 127.0.0.1 --port 8001
