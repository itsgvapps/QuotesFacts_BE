package com.gvapps.quotesfacts.model;

import java.time.LocalDateTime;

public record AnalyticsEventInsertRow(
        String eventUuid,
        String uniqueId,
        String sessionId,

        String packageName,
        String appVersion,
        String countryCode,
        String language,
        String timezone,
        String deviceOs,
        String deviceModel,
        String osVersion,

        int eventTypeId,
        String eventGroup,
        String eventKey,
        int eventCount,

        String screenName,
        String sourceScreen,
        String contentType,
        String contentId,
        String categoryId,
        String categoryName,

        String notificationType,
        String campaignId,

        String adNetwork,
        String adUnitId,
        String adPlacement,

        String metadataJson,
        LocalDateTime occurredAt
) {
}