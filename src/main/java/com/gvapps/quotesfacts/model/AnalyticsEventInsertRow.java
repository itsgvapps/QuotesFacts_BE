package com.gvapps.quotesfacts.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AnalyticsEventInsertRow(
        String uniqueId,
        String eventUuid,
        String sessionId,

        String appId,
        String packageName,
        String appVersion,

        String countryCode,
        String language,
        String timezone,

        String deviceOs,
        String deviceModel,
        String osVersion,

        String eventName,
        String eventCategory,
        int eventCount,
        BigDecimal eventValue,

        String screenName,
        String screenClass,
        String sourceScreen,

        String contentType,
        String itemId,
        String itemName,
        String itemCategory,
        String itemCategoryId,
        String itemListId,
        String itemListName,

        String searchTerm,

        String campaignId,
        String campaignName,
        String notificationType,

        String adNetwork,
        String adUnitId,
        String adFormat,
        String adPlacement,

        String eventParamsJson,
        LocalDateTime occurredAt
) {
}