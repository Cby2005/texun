package com.agriculture.agent.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AgentDiagnosisResponse {
    private boolean success;
    private String message;
    private String suggestion;
    @JsonProperty("score")
    private Integer score;
}
