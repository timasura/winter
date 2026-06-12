package com.example.winter.controller;

import com.example.winter.model.CacheEntry;
import com.example.winter.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CacheControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CacheService cacheService;

    @BeforeEach
    void cleanUp() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void set_shouldReturn201() throws Exception {
        mockMvc.perform(post("/api/cache")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"hello\",\"value\":\"world\"}"))
                .andExpect(status().isCreated());

        assertThat(cacheService.get("hello")).isEqualTo("world");
    }

    @Test
    void set_withTtl_shouldReturn201() throws Exception {
        mockMvc.perform(post("/api/cache")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"ttl-key\",\"value\":\"ttl-val\",\"ttl\":60}"))
                .andExpect(status().isCreated());

        assertThat(cacheService.get("ttl-key")).isEqualTo("ttl-val");
    }

    @Test
    void get_existingKey_shouldReturn200() throws Exception {
        cacheService.set(new CacheEntry("hello", "world", null));

        mockMvc.perform(get("/api/cache/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("world"));
    }

    @Test
    void get_nonExistingKey_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/cache/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existingKey_shouldReturn204() throws Exception {
        cacheService.set(new CacheEntry("del-key", "del-val", null));

        mockMvc.perform(delete("/api/cache/del-key"))
                .andExpect(status().isNoContent());

        assertThat(cacheService.get("del-key")).isNull();
    }

    @Test
    void delete_nonExistingKey_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/cache/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void keys_shouldReturnAllKeys() throws Exception {
        cacheService.set(new CacheEntry("key1", "val1", null));
        cacheService.set(new CacheEntry("key2", "val2", null));

        mockMvc.perform(get("/api/cache"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void exists_existingKey_shouldReturnTrue() throws Exception {
        cacheService.set(new CacheEntry("exist-key", "exist-val", null));

        mockMvc.perform(get("/api/cache/exist-key/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void exists_nonExistingKey_shouldReturnFalse() throws Exception {
        mockMvc.perform(get("/api/cache/nonexistent/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
