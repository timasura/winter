package com.example.winter.service;

import com.example.winter.model.CacheEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CacheServiceTest {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void cleanUp() {
        // 清空测试数据
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void setAndGet_withoutTtl() {
        CacheEntry entry = new CacheEntry("test:key1", "hello", null);
        cacheService.set(entry);

        String value = cacheService.get("test:key1");
        assertThat(value).isEqualTo("hello");
    }

    @Test
    void setAndGet_withTtl() {
        CacheEntry entry = new CacheEntry("test:key2", "world", 300L);
        cacheService.set(entry);

        String value = cacheService.get("test:key2");
        assertThat(value).isEqualTo("world");
    }

    @Test
    void get_nonExistingKey_returnsNull() {
        String value = cacheService.get("test:nonexistent");
        assertThat(value).isNull();
    }

    @Test
    void delete_existingKey_returnsTrue() {
        redisTemplate.opsForValue().set("test:key3", "value3");
        Boolean result = cacheService.delete("test:key3");
        assertThat(result).isTrue();
        assertThat(cacheService.get("test:key3")).isNull();
    }

    @Test
    void delete_nonExistingKey_returnsFalse() {
        Boolean result = cacheService.delete("test:nonexistent");
        assertThat(result).isFalse();
    }

    @Test
    void keys_returnsAllKeys() {
        redisTemplate.opsForValue().set("test:a", "1");
        redisTemplate.opsForValue().set("test:b", "2");

        Set<String> keys = cacheService.keys();
        assertThat(keys).contains("test:a", "test:b");
    }

    @Test
    void exists_existingKey_returnsTrue() {
        redisTemplate.opsForValue().set("test:key4", "value4");
        assertThat(cacheService.exists("test:key4")).isTrue();
    }

    @Test
    void exists_nonExistingKey_returnsFalse() {
        assertThat(cacheService.exists("test:nonexistent")).isFalse();
    }

    @Test
    void set_overwritesExistingKey() {
        redisTemplate.opsForValue().set("test:key5", "old");
        CacheEntry entry = new CacheEntry("test:key5", "new", null);
        cacheService.set(entry);

        assertThat(cacheService.get("test:key5")).isEqualTo("new");
    }
}
