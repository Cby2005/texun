package com.agri.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Slf4j
public class JwtUtils {

    private static final String DEFAULT_SECRET = "AgriChainControlSmartFarmPlatform2024SecretKeyForJWTTokenGeneration";
    private static final long DEFAULT_EXPIRE = 24 * 60 * 60 * 1000L; // 24小时
    private static final long REFRESH_EXPIRE = 7 * 24 * 60 * 60 * 1000L; // 7天

    private final SecretKey key;
    private final long accessExpire;
    private final long refreshExpire;

    public JwtUtils() {
        this(DEFAULT_SECRET, DEFAULT_EXPIRE, REFRESH_EXPIRE);
    }

    public JwtUtils(String secret, long accessExpire, long refreshExpire) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpire = accessExpire;
        this.refreshExpire = refreshExpire;
    }

    /**
     * 生成 AccessToken
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        return buildToken(claims, accessExpire);
    }

    /**
     * 生成 RefreshToken
     */
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return buildToken(claims, refreshExpire);
    }

    private String buildToken(Map<String, Object> claims, long expire) {
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expire))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 获取用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从 Token 获取角色
     */
    public String getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期");
        } catch (MalformedJwtException e) {
            log.warn("Token格式错误");
        } catch (SignatureException e) {
            log.warn("Token签名错误");
        } catch (IllegalArgumentException e) {
            log.warn("Token参数异常");
        }
        return false;
    }

    /**
     * 判断是否为 RefreshToken
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取过期时间（毫秒）
     */
    public long getAccessExpireMs() {
        return accessExpire;
    }
}
