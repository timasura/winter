package com.example.winter.service;

import com.example.winter.model.CacheEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务：基于 Redis 的通用 KV 操作
 */
@Service
@RequiredArgsConstructor
public class CacheService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 设置缓存
     */
    public void set(CacheEntry entry) {
        if (entry.getTtl() != null && entry.getTtl() > 0) {
            redisTemplate.opsForValue().set(entry.getKey(), entry.getValue(), entry.getTtl(), TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 获取缓存值
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 列出所有 key
     */
    public Set<String> keys() {
        return redisTemplate.keys("*");
    }

    /**
     * 判断 key 是否存在
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
}
