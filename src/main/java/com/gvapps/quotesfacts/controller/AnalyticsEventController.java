package com.gvapps.quotesfacts.controller;

import com.gvapps.quotesfacts.dto.request.AnalyticsEventRequest;
import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.service.AnalyticsEventService;
import com.gvapps.quotesfacts.util.ResponseUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsEventController {

    private final AnalyticsEventService analyticsEventService;

    public AnalyticsEventController(AnalyticsEventService analyticsEventService) {
        this.analyticsEventService = analyticsEventService;
    }

    @PostMapping("/events")
    public ResponseEntity<APIResponse> trackEvents(
            @Valid @RequestBody AnalyticsEventRequest request
    ) {
        analyticsEventService.ingestEvents(request);
        return ResponseEntity.ok(ResponseUtils.success(
                "200", "Analytics events tracked successfully", null
        ));
    }
}