package com.agriculture.agent.service;

import com.agriculture.agent.dto.AgentDiagnosisRequest;
import com.agriculture.agent.dto.AgentDiagnosisResponse;
import com.agriculture.yolo.dto.YoloDetectResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentDiagnosisExecutor {

    @Value("${app.agent.diagnosis-url:http://localhost:8002/diagnose}")
    private String agentUrl;
    @Value("${app.yolo.confidence-threshold:0.50}")
    private double confThreshold;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public record DiagnosisResult(String suggestion, int score, boolean autoReply) {}

    public DiagnosisResult diagnose(Long questionId, String cropType, String description,
                                     String imageUrl, YoloDetectResponse yoloResult) {
        if (!yoloResult.hasDetection())
            return new DiagnosisResult("系统未能识别到明显病虫害目标，建议转交专家人工诊断。", 0, false);

        AgentDiagnosisRequest req = AgentDiagnosisRequest.builder()
                .questionId(questionId).cropType(cropType != null ? cropType : "未知")
                .farmerDescription(description != null ? description : "")
                .imageUrl(imageUrl)
                .yoloResult(AgentDiagnosisRequest.YoloResultInfo.builder()
                    .diseaseName(yoloResult.getTopDiseaseName())
                    .confidence(yoloResult.getTopConfidence()).build())
                .build();

        try {
            HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<String> resp = restTemplate.postForEntity(agentUrl, new HttpEntity<>(req, h), String.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                AgentDiagnosisResponse dr = objectMapper.readValue(resp.getBody(), AgentDiagnosisResponse.class);
                if (dr.isSuccess()) {
                    boolean autoReply = dr.getScore() != null && dr.getScore() >= 90
                        && yoloResult.getTopConfidence() != null && yoloResult.getTopConfidence() >= confThreshold;
                    return new DiagnosisResult(dr.getSuggestion(), dr.getScore() != null ? dr.getScore() : 0, autoReply);
                }
            }
        } catch (Exception e) { log.error("Agent诊断失败", e); }
        return fallback(yoloResult.getTopDiseaseName(), yoloResult.getTopConfidence() != null ? yoloResult.getTopConfidence() : 0);
    }

    private DiagnosisResult fallback(String disease, double conf) {
        StringBuilder sb = new StringBuilder("【本地规则诊断】\n\n识别病虫害：").append(disease)
            .append("\n置信度：").append(String.format("%.0f%%", conf * 100)).append("\n\n");
        String l = disease.toLowerCase();
        if (l.contains("spot") || l.contains("斑")) sb.append("叶斑病类：清除病叶，多菌灵500倍液喷雾。");
        else if (l.contains("blight") || l.contains("枯")) sb.append("枯萎病类：拔除病株，恶霉灵灌根。");
        else if (l.contains("mildew") || l.contains("霉") || l.contains("粉")) sb.append("白粉/霉病类：三唑酮喷雾，控制氮肥。");
        else if (l.contains("worm") || l.contains("虫")) sb.append("虫害：阿维菌素喷雾，设置诱虫灯。");
        else if (l.contains("rot") || l.contains("腐")) sb.append("腐烂病类：清除腐烂组织，春雷霉素防治。");
        else sb.append("建议咨询当地农技站确认病害类型。");
        sb.append("\n\n⚠ 识别置信度较低，建议转专家复核。");
        return new DiagnosisResult(sb.toString(), 0, false);
    }
}
