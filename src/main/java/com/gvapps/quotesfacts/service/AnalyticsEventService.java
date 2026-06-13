package com.gvapps.quotesfacts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gvapps.quotesfacts.dto.request.AnalyticsEventRequest;
import com.gvapps.quotesfacts.model.AnalyticsEventInsertRow;
import com.gvapps.quotesfacts.repository.AnalyticsEventRepository;
import com.gvapps.quotesfacts.util.AnalyticsNameNormalizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsEventService {

    private final AnalyticsEventRepository repository;
    private final AnalyticsNameNormalizer normalizer;
    private final ObjectMapper objectMapper;
    private final int maxEventsPerRequest;

    public AnalyticsEventService(
            AnalyticsEventRepository repository,
            AnalyticsNameNormalizer normalizer,
            ObjectMapper objectMapper,
            @Value("${app.analytics.max-events-per-request:200}") int maxEventsPerRequest
    ) {
        this.repository = repository;
        this.normalizer = normalizer;
        this.objectMapper = objectMapper;
        this.maxEventsPerRequest = maxEventsPerRequest;
    }

    @Transactional
    public void ingestEvents(AnalyticsEventRequest request) {
        if (request.events().size() > maxEventsPerRequest) {
            throw new IllegalArgumentException("Maximum allowed events per request is " + maxEventsPerRequest);
        }

        List<AnalyticsEventInsertRow> insertRows = new ArrayList<>();

        for (AnalyticsEventRequest.EventItem event : request.events()) {
            AnalyticsEventInsertRow row = toInsertRow(request, event);

            if (row != null) {
                insertRows.add(row);
            }
        }

        repository.insertEventsIgnoreDuplicates(insertRows);
    }

    private AnalyticsEventInsertRow toInsertRow(
            AnalyticsEventRequest request,
            AnalyticsEventRequest.EventItem event
    ) {
        String eventName = normalizer.normalize(event.eventName());
        String eventCategory = normalizer.normalize(event.eventCategory());

        if (!normalizer.isValidAnalyticsName(eventName)) {
            return null;
        }

        if (!normalizer.isValidAnalyticsName(eventCategory)) {
            return null;
        }

        int eventCount = event.safeCount();

        if (eventCount <= 0) {
            return null;
        }

        return new AnalyticsEventInsertRow(
                trim(request.uniqueId()),
                trim(event.eventUuid()),
                trim(request.sessionId()),

                trim(request.appId()),
                trim(request.packageName()),
                trim(request.appVersion()),

                trim(request.countryCode()),
                trim(request.language()),
                trim(request.timezone()),

                trim(request.deviceOs()),
                trim(request.deviceModel()),
                trim(request.osVersion()),

                eventName,
                eventCategory,
                eventCount,
                event.eventValue(),

                trim(event.screenName()),
                trim(event.screenClass()),
                trim(event.sourceScreen()),

                trim(event.contentType()),
                trim(event.itemId()),
                trim(event.itemName()),
                trim(event.itemCategory()),
                trim(event.itemCategoryId()),
                trim(event.itemListId()),
                trim(event.itemListName()),

                trim(event.searchTerm()),

                trim(event.campaignId()),
                trim(event.campaignName()),
                trim(event.notificationType()),

                trim(event.adNetwork()),
                trim(event.adUnitId()),
                trim(event.adFormat()),
                trim(event.adPlacement()),

                toJson(event.eventParams()),
                event.occurredAt() == null ? LocalDateTime.now() : event.occurredAt()
        );
    }

    private String toJson(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid eventParams JSON");
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}