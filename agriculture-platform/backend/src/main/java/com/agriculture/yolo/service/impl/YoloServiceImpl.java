package com.agriculture.yolo.service.impl;

import com.agriculture.yolo.dto.YoloDetectResponse;
import com.agriculture.yolo.dto.YoloPredictionDTO;
import com.agriculture.yolo.entity.KnowledgeYoloResult;
import com.agriculture.yolo.mapper.KnowledgeYoloResultMapper;
import com.agriculture.yolo.service.YoloService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoloServiceImpl implements YoloService {

    private static final Set<String> ALLOWED = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "webp"));
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    @Value("${app.yolo.provider:roboflow}")
    private String provider;

    @Value("${app.yolo.service-url:http://localhost:8001/detect}")
    private String yoloUrl;

    @Value("${app.yolo.confidence-threshold:0.50}")
    private double confidenceThreshold;

    @Value("${app.roboflow.api-url:https://serverless.roboflow.com}")
    private String roboflowApiUrl;

    @Value("${app.roboflow.api-key:}")
    private String roboflowApiKey;

    @Value("${app.roboflow.model-id:pest-detection-yolov8/1}")
    private String roboflowModelId;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KnowledgeYoloResultMapper yoloResultMapper;

    @Override
    public YoloDetectResponse detectDisease(MultipartFile file) {
        return detectDisease(file, null, null);
    }

    @Override
    public YoloDetectResponse detectDisease(MultipartFile file, Long questionId, Long imageId) {
        YoloDetectResponse validationError = validate(file);
        if (validationError != null) {
            return validationError;
        }

        YoloDetectResponse response = isRoboflowProvider()
                ? callRoboflow(file)
                : callLocalYolo(file);

        if (questionId != null) {
            saveDetectionResult(response, questionId, imageId);
        }
        return response;
    }

    private YoloDetectResponse validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return err("上传图片不能为空");
        }
        String ext = getExt(file.getOriginalFilename());
        if (!ALLOWED.contains(ext)) {
            return err("不支持的图片格式: " + ext);
        }
        if (file.getSize() > MAX_SIZE) {
            return err("图片不能超过5MB");
        }
        return null;
    }

    private boolean isRoboflowProvider() {
        return "roboflow".equalsIgnoreCase(provider);
    }

    private YoloDetectResponse callLocalYolo(MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartResource(file));
            body.add("conf", confidenceThreshold);

            ResponseEntity<String> resp = restTemplate.postForEntity(yoloUrl, new HttpEntity<>(body, headers), String.class);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                return err("YOLO服务异常: " + resp.getStatusCode().value());
            }

            YoloDetectResponse response = objectMapper.readValue(resp.getBody(), YoloDetectResponse.class);
            response.setProvider("local");
            response.setRawJson(resp.getBody());
            return response;
        } catch (Exception e) {
            log.error("本地YOLO调用失败", e);
            return err("YOLO检测服务不可用");
        }
    }

    private YoloDetectResponse callRoboflow(MultipartFile file) {
        if (roboflowApiKey == null || roboflowApiKey.isBlank()) {
            return err("未配置Roboflow API Key");
        }

        try {
            ResponseEntity<String> resp;
            try {
                resp = postRoboflowMultipart(file);
            } catch (RestClientException multipartError) {
                log.warn("Roboflow multipart调用失败，改用base64请求体重试");
                resp = postRoboflowBase64(file);
            }

            if (!resp.getStatusCode().is2xxSuccessful()) {
                return err("Roboflow服务异常: " + resp.getStatusCode().value());
            }

            return parseRoboflowResponse(resp.getBody());
        } catch (RestClientException e) {
            log.error("Roboflow调用失败", e);
            return err("Roboflow识别服务不可用");
        } catch (Exception e) {
            log.error("Roboflow结果解析失败", e);
            return err("Roboflow识别结果解析失败");
        }
    }

    private ResponseEntity<String> postRoboflowMultipart(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartResource(file));

        return restTemplate.exchange(
                buildRoboflowUrl(false),
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class
        );
    }

    private ResponseEntity<String> postRoboflowBase64(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        return restTemplate.exchange(
                buildRoboflowUrl(true),
                HttpMethod.POST,
                new HttpEntity<>(base64Image, headers),
                String.class
        );
    }

    private String buildRoboflowUrl(boolean base64Body) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(trimTrailingSlash(roboflowApiUrl))
                .pathSegment(roboflowModelId.split("/"))
                .queryParam("api_key", roboflowApiKey)
                .queryParam("confidence", Math.round(confidenceThreshold * 100))
                .queryParam("format", "json");
        if (base64Body) {
            builder.queryParam("image_type", "base64");
        }
        return builder.toUriString();
    }

    private YoloDetectResponse parseRoboflowResponse(String rawJson) throws IOException {
        JsonNode root = objectMapper.readTree(rawJson);
        JsonNode predictionsNode = root.path("predictions");

        List<YoloPredictionDTO> predictions = new ArrayList<>();
        if (predictionsNode.isArray()) {
            for (JsonNode item : predictionsNode) {
                String className = text(item, "class", "class_name", "className", "label", "name");
                Double confidence = number(item, "confidence", "score");

                YoloPredictionDTO dto = new YoloPredictionDTO();
                dto.setClassName(className);
                dto.setConfidence(confidence);
                dto.setX(integer(item, "x"));
                dto.setY(integer(item, "y"));
                dto.setWidth(integer(item, "width", "w"));
                dto.setHeight(integer(item, "height", "h"));
                predictions.add(dto);
            }
        }

        YoloPredictionDTO top = predictions.stream()
                .filter(p -> p.getConfidence() != null)
                .max((a, b) -> Double.compare(a.getConfidence(), b.getConfidence()))
                .orElse(null);

        YoloDetectResponse response = new YoloDetectResponse();
        response.setSuccess(true);
        response.setMessage(predictions.isEmpty() ? "未识别到病虫害目标" : "识别成功");
        response.setProvider("roboflow");
        response.setModelId(roboflowModelId);
        response.setPredictions(predictions);
        response.setRawJson(rawJson);
        if (top != null) {
            response.setTopDiseaseName(top.getClassName());
            response.setTopConfidence(top.getConfidence());
        }
        return response;
    }

    private void saveDetectionResult(YoloDetectResponse response, Long questionId, Long imageId) {
        try {
            KnowledgeYoloResult result = new KnowledgeYoloResult();
            result.setQuestionId(questionId);
            result.setImageId(imageId);
            result.setModelId(response.getModelId() != null ? response.getModelId() : modelIdForCurrentProvider());
            result.setDiseaseName(response.getTopDiseaseName());
            result.setConfidence(toConfidence(response.getTopConfidence()));
            result.setPredictionJson(response.getRawJson());
            result.setCreateTime(LocalDateTime.now());
            result.setDeleted(0);
            yoloResultMapper.insert(result);
            response.setResultId(result.getId());
        } catch (Exception e) {
            log.warn("YOLO识别结果落库失败 questionId={}", questionId, e);
        }
    }

    private String modelIdForCurrentProvider() {
        return isRoboflowProvider() ? roboflowModelId : "local-yolo";
    }

    private BigDecimal toConfidence(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
    }

    private String getExt(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "https://serverless.roboflow.com";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String text(JsonNode node, String... keys) {
        for (String key : keys) {
            JsonNode value = node.get(key);
            if (value != null && !value.isNull()) {
                return value.asText();
            }
        }
        return null;
    }

    private Double number(JsonNode node, String... keys) {
        for (String key : keys) {
            JsonNode value = node.get(key);
            if (value != null && value.isNumber()) {
                return value.asDouble();
            }
        }
        return null;
    }

    private Integer integer(JsonNode node, String... keys) {
        Double value = number(node, keys);
        return value == null ? null : (int) Math.round(value);
    }

    private YoloDetectResponse err(String msg) {
        YoloDetectResponse response = new YoloDetectResponse();
        response.setSuccess(false);
        response.setMessage(msg);
        response.setProvider(isRoboflowProvider() ? "roboflow" : "local");
        response.setModelId(modelIdForCurrentProvider());
        return response;
    }

    private static class MultipartResource extends InputStreamResource {
        private final String filename;

        MultipartResource(MultipartFile file) throws IOException {
            super(file.getInputStream());
            this.filename = file.getOriginalFilename();
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public long contentLength() {
            return -1;
        }
    }
}
