package com.agriculture.agri.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfig {
    /** DeepSeek API Key，必须在 application.yml 或环境变量中配置 */
    private String apiKey = "";
    /** DeepSeek API 基础地址 */
    private String baseUrl = "https://api.deepseek.com";
    /** 模型名称 */
    private String model = "deepseek-chat";
    /** 超时时间（秒） */
    private int timeout = 60;
    /** 最大输出 token 数 */
    private int maxTokens = 2048;
    /** 温度参数，控制回答的随机性 */
    private double temperature = 0.7;

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    /** 返回脱敏后的 API Key 用于日志 */
    public String getMaskedApiKey() {
        if (apiKey == null || apiKey.length() <= 8) return "***";
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
