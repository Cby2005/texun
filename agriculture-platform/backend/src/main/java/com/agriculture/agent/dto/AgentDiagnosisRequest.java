package com.agriculture.agent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentDiagnosisRequest {
    private Long questionId;
    private String cropType;
    private String farmerDescription;
    private String imageUrl;
    private YoloResultInfo yoloResult;

    @Data @Builder
    public static class YoloResultInfo {
        private String diseaseName;
        private Double confidence;
    }
}
