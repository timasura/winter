package com.example.winter.controller;

import com.example.winter.model.CacheEntry;
import com.example.winter.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 缓存 REST API
 */
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    /**
     * 设置缓存
     */
    @PostMapping
    public ResponseEntity<Void> set(@RequestBody CacheEntry entry) {
        cacheService.set(entry);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 获取缓存值
     */
    @GetMapping("/{key}")
    public ResponseEntity<String> get(@PathVariable String key) {
        String value = cacheService.get(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    /**
     * 删除缓存
     */
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        Boolean deleted = cacheService.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 列出所有 key
     */
    @GetMapping
    public ResponseEntity<Set<String>> keys() {
        Set<String> keys = cacheService.keys();
        return ResponseEntity.ok(keys);
    }

    /**
     * 判断 key 是否存在
     */
    @GetMapping("/{key}/exists")
    public ResponseEntity<Boolean> exists(@PathVariable String key) {
        return ResponseEntity.ok(cacheService.exists(key));
    }
}
