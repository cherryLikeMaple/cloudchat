package com.cherry.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cherry
 */
@RestController
@RequestMapping("/auth")
public class HelloController {
    
    @GetMapping("/health")
    public String hello() {
        return "hello";
    }
}
