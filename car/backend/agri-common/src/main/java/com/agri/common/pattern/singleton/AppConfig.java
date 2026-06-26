package com.agri.common.pattern.singleton;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例模式：全局配置中心
 * 通过 Spring 单例 + ConcurrentHashMap 实现线程安全的全局配置管理
 */
@Component
public class AppConfig {

    private final Map<String, String> configMap = new ConcurrentHashMap<>();

    public String get(String key) {
        return configMap.get(key);
    }

    public String get(String key, String defaultValue) {
        return configMap.getOrDefault(key, defaultValue);
    }

    public void put(String key, String value) {
        configMap.put(key, value);
    }

    public void remove(String key) {
        configMap.remove(key);
    }

    public boolean containsKey(String key) {
        return configMap.containsKey(key);
    }

    public Map<String, String> getAll() {
        return Map.copyOf(configMap);
    }
}
