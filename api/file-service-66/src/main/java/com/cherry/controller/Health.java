package com.cherry.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cherry
 */
@RestController
@RequestMapping(("/file"))
public class Health {
    
    @GetMapping("/health")
    public String health() {
        return "FILE_OK";
    }
}
