package com.gvapps.quotesfacts.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record AnalyticsEventRequest(

        @NotBlank(message = "uniqueId is required")
        @Size(max = 255, message = "uniqueId must be less than 255 characters")
        String uniqueId,

        @Size(max = 100, message = "sessionId must be less than 100 characters")
        String sessionId,

        @Size(max = 100, message = "appId must be less than 100 characters")
        String appId,

        @Size(max = 255, message = "packageName must be less than 255 characters")
        String packageName,

        @Size(max = 100, message = "appVersion must be less than 100 characters")
        String appVersion,

        @Size(max = 50, message = "countryCode must be less than 50 characters")
        String countryCode,

        @Size(max = 50, message = "language must be less than 50 characters")
        String language,

        @Size(max = 100, message = "timezone must be less than 100 characters")
        String timezone,

        @Size(max = 100, message = "deviceOs must be less than 100 characters")
        String deviceOs,

        @Size(max = 150, message = "deviceModel must be less than 150 characters")
        String deviceModel,

        @Size(max = 100, message = "osVersion must be less than 100 characters")
        String osVersion,

        @NotEmpty(message = "events cannot be empty")
        @Valid
        List<EventItem> events
) {

    public record EventItem(

            @NotBlank(message = "eventUuid is required")
            @Size(max = 100, message = "eventUuid must be less than 100 characters")
            String eventUuid,

            @NotBlank(message = "eventName is required")
            @Size(max = 80, message = "eventName must be less than 80 characters")
            String eventName,

            @NotBlank(message = "eventCategory is required")
            @Size(max = 50, message = "eventCategory must be less than 50 characters")
            String eventCategory,

            @Size(max = 80, message = "eventGroup must be less than 80 characters")
            String eventGroup,

            @Min(value = 1, message = "count must be at least 1")
            @Max(value = 100000, message = "count is too large")
            Integer count,

            BigDecimal eventValue,

            @Size(max = 100, message = "screenName must be less than 100 characters")
            String screenName,

            @Size(max = 100, message = "screenClass must be less than 100 characters")
            String screenClass,

            @Size(max = 100, message = "sourceScreen must be less than 100 characters")
            String sourceScreen,

            @Size(max = 50, message = "contentType must be less than 50 characters")
            String contentType,

            @Size(max = 100, message = "itemId must be less than 100 characters")
            String itemId,

            @Size(max = 255, message = "itemName must be less than 255 characters")
            String itemName,

            @Size(max = 150, message = "itemCategory must be less than 150 characters")
            String itemCategory,

            @Size(max = 100, message = "itemCategoryId must be less than 100 characters")
            String itemCategoryId,

            @Size(max = 100, message = "itemListId must be less than 100 characters")
            String itemListId,

            @Size(max = 150, message = "itemListName must be less than 150 characters")
            String itemListName,

            @Size(max = 255, message = "searchTerm must be less than 255 characters")
            String searchTerm,

            @Size(max = 100, message = "campaignId must be less than 100 characters")
            String campaignId,

            @Size(max = 150, message = "campaignName must be less than 150 characters")
            String campaignName,

            @Size(max = 80, message = "notificationType must be less than 80 characters")
            String notificationType,

            @Size(max = 80, message = "adNetwork must be less than 80 characters")
            String adNetwork,

            @Size(max = 150, message = "adUnitId must be less than 150 characters")
            String adUnitId,

            @Size(max = 80, message = "adFormat must be less than 80 characters")
            String adFormat,

            @Size(max = 100, message = "adPlacement must be less than 100 characters")
            String adPlacement,

            Map<String, Object> eventParams,

            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime occurredAt
    ) {
        public int safeCount() {
            return count == null ? 1 : count;
        }
    }
}
