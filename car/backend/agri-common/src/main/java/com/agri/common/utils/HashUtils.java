package com.agri.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

/**
 * 哈希工具类 - 用于哈希链存证
 */
public final class HashUtils {

    private static final ObjectMapper CANONICAL_MAPPER;

    static {
        CANONICAL_MAPPER = new ObjectMapper();
        CANONICAL_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    private HashUtils() {}

    /**
     * SHA-256 哈希
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * 将对象转为 Canonical JSON 后计算 SHA-256
     */
    public static String sha256OfObject(Object obj) {
        String json = toCanonicalJson(obj);
        return sha256(json);
    }

    /**
     * 转为 Canonical JSON（字段排序，确保一致性）
     */
    public static String toCanonicalJson(Object obj) {
        try {
            // 将对象转为 TreeMap 保证字段排序
            TreeMap<String, Object> sorted = CANONICAL_MAPPER.readValue(
                    CANONICAL_MAPPER.writeValueAsString(obj),
                    CANONICAL_MAPPER.getTypeFactory().constructMapType(TreeMap.class, String.class, Object.class)
            );
            return CANONICAL_MAPPER.writeValueAsString(sorted);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    /**
     * 计算区块哈希
     * current_hash = SHA256(chain_id + batch_no + business_type + business_id + data_hash + previous_hash + create_time)
     */
    public static String computeBlockHash(String chainId, String batchNo, String businessType,
                                           String businessId, String dataHash, String previousHash,
                                           String createTime) {
        String input = chainId + batchNo + businessType + businessId + dataHash + previousHash + createTime;
        return sha256(input);
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
