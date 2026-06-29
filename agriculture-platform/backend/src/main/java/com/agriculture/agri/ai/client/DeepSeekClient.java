package com.agriculture.agri.ai.client;

import com.agriculture.agri.ai.config.DeepSeekConfig;
import com.agriculture.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSeekClient {

    private final DeepSeekConfig config;
    private final RestTemplate restTemplate;

    private static final String CHAT_COMPLETIONS_PATH = "/v1/chat/completions";

    /**
     * 调用 DeepSeek Chat API（非流式）
     * @param messages 消息列表
     * @return { content, promptTokens, completionTokens, totalTokens }
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> chat(List<Map<String, String>> messages) {
        if (!config.isConfigured()) {
            throw new BizException(500, "AI 服务未配置，请联系管理员设置 DeepSeek API Key");
        }

        String url = config.getBaseUrl() + CHAT_COMPLETIONS_PATH;
        log.info("调用 DeepSeek API, model={}, url={}, apiKey={}", config.getModel(), url, config.getMaskedApiKey());

        // 构建请求体
        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("messages", messages);
        body.put("max_tokens", config.getMaxTokens());
        body.put("temperature", config.getTemperature());
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getApiKey());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            // 设置超时
            RestTemplate rt = buildTimeoutRestTemplate();
            ResponseEntity<Map> response = rt.exchange(url, HttpMethod.POST, request, Map.class);

            Map<String, Object> respBody = response.getBody();
            if (respBody == null) {
                log.error("DeepSeek API 返回空响应");
                throw new BizException(500, "AI 服务返回为空，请稍后重试");
            }

            // 提取 choices[0].message.content
            List<Map<String, Object>> choices = (List<Map<String, Object>>) respBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                log.error("DeepSeek API 返回无 choices, body={}", respBody);
                throw new BizException(500, "AI 服务返回异常，请稍后重试");
            }
            Map<String, Object> choice = choices.get(0);
            Map<String, String> message = (Map<String, String>) choice.get("message");
            String content = message != null ? message.get("content") : null;

            if (content == null || content.isBlank()) {
                log.warn("DeepSeek 回答为空");
                throw new BizException(500, "当前问题较复杂，建议转专家复核");
            }

            // 提取 usage
            Map<String, Object> usage = (Map<String, Object>) respBody.get("usage");
            int promptTokens = usage != null ? ((Number) usage.getOrDefault("prompt_tokens", 0)).intValue() : 0;
            int completionTokens = usage != null ? ((Number) usage.getOrDefault("completion_tokens", 0)).intValue() : 0;
            int totalTokens = usage != null ? ((Number) usage.getOrDefault("total_tokens", 0)).intValue() : 0;

            log.info("DeepSeek API 调用成功, totalTokens={}", totalTokens);

            Map<String, Object> result = new HashMap<>();
            result.put("content", content);
            result.put("promptTokens", promptTokens);
            result.put("completionTokens", completionTokens);
            result.put("totalTokens", totalTokens);
            return result;

        } catch (ResourceAccessException e) {
            log.error("DeepSeek API 连接超时", e);
            throw new BizException(500, "AI 服务连接超时，请稍后重试");
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("DeepSeek API 调用失败", e);
            throw new BizException(500, "AI 服务暂时不可用，请稍后重试");
        }
    }

    private RestTemplate buildTimeoutRestTemplate() {
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(config.getTimeout() * 1000);
        factory.setReadTimeout(config.getTimeout() * 1000);
        return new RestTemplate(factory);
    }

    /**
     * 地理编码：根据地址通过 DeepSeek 获取经纬度
     * @param address 地址字符串
     * @return Map { lng: 经度, lat: 纬度 }，失败返回 null
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> geocode(String address) {
        if (address == null || address.isBlank()) return null;
        if (!config.isConfigured()) {
            log.info("DeepSeek 未配置 API Key，跳过地理编码");
            return null;
        }
        String prompt = "请解析以下地址的经纬度：\n" + address.trim() +
                "\n\n只返回纯JSON格式，不要包含任何其他文字：{\"lng\": 经度数字, \"lat\": 纬度数字}" +
                "\n如果无法解析，返回：{\"lng\": null, \"lat\": null}";
        try {
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            Map<String, Object> resp = chat(messages);
            String content = (String) resp.get("content");
            if (content == null) return null;
            // 提取 JSON
            content = content.trim();
            int jsonStart = content.indexOf('{');
            int jsonEnd = content.lastIndexOf('}');
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                content = content.substring(jsonStart, jsonEnd + 1);
            }
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> geo = mapper.readValue(content, Map.class);
            if (geo.get("lng") != null && geo.get("lat") != null) {
                log.info("地理编码成功: {} -> lng={}, lat={}", address, geo.get("lng"), geo.get("lat"));
                return geo;
            }
        } catch (Exception e) {
            log.warn("地理编码失败: {} - {}", address, e.getMessage());
        }
        return null;
    }

    /**
     * 构建带系统提示的消息列表
     */
    public List<Map<String, String>> buildMessages(String systemPrompt, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userMessage));
        return messages;
    }
}
