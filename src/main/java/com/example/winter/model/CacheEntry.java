package com.example.winter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 缓存键值对 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheEntry {

    private String key;
    private String value;
    /** 过期时间（秒），可选，不设置则永不过期 */
    private Long ttl;
}
