package com.gvapps.quotesfacts.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/v1/health")
public class HealthCheckController {

    @GetMapping("/check")
    public Map<String, Object> healthCheck() {
        log.info("[HealthCheckController] >> [check]");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now());
        return response;
    }
}
