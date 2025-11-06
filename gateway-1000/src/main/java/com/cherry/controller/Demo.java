package com.cherry.controller;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
public class Demo {
    
    @Resource
    private RedisTemplate redisTemplate;
    
    @GetMapping("/health")
    public String health() {
        redisTemplate.opsForValue().set("hello", "world");
        return "success";
    }
}
