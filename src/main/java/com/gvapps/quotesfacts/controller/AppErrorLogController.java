package com.gvapps.quotesfacts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.entity.AppErrorLogEntity;
import com.gvapps.quotesfacts.service.AppErrorLogService;
import com.gvapps.quotesfacts.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/error")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppErrorLogController {

    private final AppErrorLogService service;

    @PostMapping("/logError")
    public ResponseEntity<APIResponse> logError(@RequestBody JsonNode payload) {
        AppErrorLogEntity errorLog = new AppErrorLogEntity();
        errorLog.setPayload(payload.toString()); // store full JSON
        errorLog.setDeviceId(payload.path("deviceId").asText(null));
        errorLog.setDeviceOs(payload.path("deviceOs").asText(null));
        errorLog.setAppName(payload.path("appName").asText(null));
        errorLog.setAppVersion(payload.path("appVersion").asText(null));
        errorLog.setSessionId(payload.path("sessionId").asText(null));
        errorLog.setPackageName(payload.path("packageName").asText(null));

        AppErrorLogEntity savedLog = service.logError(errorLog);
        return ResponseEntity.ok(ResponseUtils.success(
                "200", "Log added successfully", savedLog.getId()
        ));
    }
}

