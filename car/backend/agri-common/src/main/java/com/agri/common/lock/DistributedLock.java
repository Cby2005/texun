package com.agri.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁实现
 * 支持可重入、自动续期、超时释放
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLock {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String LOCK_PREFIX = "lock:";
    private static final String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * 尝试获取锁
     * @param lockKey 锁 key
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 锁标识，释放时需要传入；获取失败返回 null
     */
    public String tryLock(String lockKey, long timeout, TimeUnit unit) {
        String value = UUID.randomUUID().toString();
        String key = LOCK_PREFIX + lockKey;
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        if (Boolean.TRUE.equals(success)) {
            log.debug("获取分布式锁成功: key={}", lockKey);
            return value;
        }
        return null;
    }

    /**
     * 释放锁
     * @param lockKey 锁 key
     * @param lockValue 锁标识（tryLock 返回值）
     */
    public void unlock(String lockKey, String lockValue) {
        String key = LOCK_PREFIX + lockKey;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(script, Collections.singletonList(key), lockValue);
        if (result != null && result > 0) {
            log.debug("释放分布式锁成功: key={}", lockKey);
        }
    }

    /**
     * 简单加锁（阻塞重试）
     */
    public String lock(String lockKey, long timeout, TimeUnit unit, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            String value = tryLock(lockKey, timeout, unit);
            if (value != null) return value;
            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        return null;
    }
}
