package com.gvapps.quotesfacts.dto.request;

import lombok.Data;

@Data
public class UpdateNotificationRequest {
    private Long id;
    private String appName;
    private Boolean notificationEnabled;
    private String fcmToken;
}
