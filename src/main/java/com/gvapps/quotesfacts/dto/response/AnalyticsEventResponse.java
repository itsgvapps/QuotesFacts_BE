package com.gvapps.quotesfacts.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AnalyticsEventResponse(
        String status,
        int totalReceived,
        int validEvents,
        int insertedEvents,
        int duplicateEvents,
        int invalidEvents,
        List<InvalidEvent> invalidEventDetails,
        LocalDateTime processedAt
) {

    public record InvalidEvent(
            String eventUuid,
            String eventGroup,
            String eventKey,
            String reason
    ) {
    }

    public static AnalyticsEventResponse success(
            int totalReceived,
            int validEvents,
            int insertedEvents,
            int duplicateEvents,
            List<InvalidEvent> invalidEventDetails
    ) {
        int invalidEvents = invalidEventDetails == null ? 0 : invalidEventDetails.size();

        String status;
        if (invalidEvents == 0) {
            status = "SUCCESS";
        } else if (insertedEvents > 0 || duplicateEvents > 0) {
            status = "PARTIAL_SUCCESS";
        } else {
            status = "FAILED";
        }

        return new AnalyticsEventResponse(
                status,
                totalReceived,
                validEvents,
                insertedEvents,
                duplicateEvents,
                invalidEvents,
                invalidEventDetails,
                LocalDateTime.now()
        );
    }
}