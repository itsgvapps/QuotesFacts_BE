package com.gvapps.quotesfacts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gvapps.quotesfacts.dto.request.AnalyticsEventRequest;
import com.gvapps.quotesfacts.model.AnalyticsEventInsertRow;
import com.gvapps.quotesfacts.model.EventTypeLookupKey;
import com.gvapps.quotesfacts.model.EventTypeRow;
import com.gvapps.quotesfacts.repository.AnalyticsEventRepository;
import com.gvapps.quotesfacts.util.AnalyticsNameNormalizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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

        List<PreparedEvent> preparedEvents = prepareEvents(request);

        Set<EventTypeLookupKey> lookupKeys = new HashSet<>();
        for (PreparedEvent preparedEvent : preparedEvents) {
            lookupKeys.add(new EventTypeLookupKey(preparedEvent.eventGroup(), preparedEvent.eventKey()));
        }

        Map<EventTypeLookupKey, EventTypeRow> activeEventTypes = repository.findActiveEventTypes(lookupKeys);

        List<AnalyticsEventInsertRow> insertRows = new ArrayList<>();

        for (PreparedEvent preparedEvent : preparedEvents) {
            EventTypeLookupKey key = new EventTypeLookupKey(
                    preparedEvent.eventGroup(),
                    preparedEvent.eventKey()
            );

            EventTypeRow eventType = activeEventTypes.get(key);

            if (eventType == null) {
                continue;
            }

            insertRows.add(toInsertRow(request, preparedEvent, eventType));
        }

        repository.insertEventsIgnoreDuplicates(insertRows);
    }

    private List<PreparedEvent> prepareEvents(AnalyticsEventRequest request) {
        List<PreparedEvent> preparedEvents = new ArrayList<>();

        for (AnalyticsEventRequest.EventItem event : request.events()) {
            String eventGroup = normalizer.normalize(event.eventGroup());
            String eventKey = normalizer.normalize(event.eventKey());

            preparedEvents.add(new PreparedEvent(
                    event.eventUuid(),
                    eventGroup,
                    eventKey,
                    event.safeCount(),
                    event
            ));
        }

        return preparedEvents;
    }

    private AnalyticsEventInsertRow toInsertRow(
            AnalyticsEventRequest request,
            PreparedEvent preparedEvent,
            EventTypeRow eventType
    ) {
        AnalyticsEventRequest.EventItem event = preparedEvent.originalEvent();

        return new AnalyticsEventInsertRow(
                trim(event.eventUuid()),
                trim(request.uniqueId()),
                trim(request.sessionId()),

                trim(request.packageName()),
                trim(request.appVersion()),
                trim(request.countryCode()),
                trim(request.language()),
                trim(request.timezone()),
                trim(request.deviceOs()),
                trim(request.deviceModel()),
                trim(request.osVersion()),

                eventType.id(),
                eventType.eventGroup(),
                eventType.eventKey(),
                preparedEvent.count(),

                trim(event.screenName()),
                trim(event.sourceScreen()),
                trim(event.contentType()),
                trim(event.contentId()),
                trim(event.categoryId()),
                trim(event.categoryName()),

                trim(event.notificationType()),
                trim(event.campaignId()),

                trim(event.adNetwork()),
                trim(event.adUnitId()),
                trim(event.adPlacement()),

                toJson(event.metadata()),
                event.occurredAt() == null ? LocalDateTime.now() : event.occurredAt()
        );
    }

    private String toJson(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid metadata JSON");
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private record PreparedEvent(
            String eventUuid,
            String eventGroup,
            String eventKey,
            int count,
            AnalyticsEventRequest.EventItem originalEvent
    ) {
    }
}