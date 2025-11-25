package com.gvapps.quotesfacts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String deviceId;
    private String deviceOs;
    private String appId;
    private String packageName;
    private String fcmToken;
    private String name;
    private String userName;
    private String email;
    private String countryCode;
    private String language;
    private String appVersion;
    private String osVersion;
    private String deviceModel;
    private String timezone;
    private Boolean emulator;
    private Boolean active;
    private Boolean isPremium;
    private Boolean darkMode;
    private Boolean notificationEnabled;
    private Integer syncStatus;
    private Integer sessionCount;
    private Integer appOpenCount;
    private Integer appRating;
    private Float engagementScore;
    private LocalDateTime lastLogin;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
