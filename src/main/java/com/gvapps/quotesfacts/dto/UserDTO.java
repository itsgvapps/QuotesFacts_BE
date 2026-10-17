package com.gvapps.quotesfacts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String uniqueId;
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Map<String, Object>> notificationCounts;
    private Integer syncStatus;
    private Integer sessionCount;
    private Integer appOpenCount;
    private Integer appRating;
    private Float engagementScore;
    private LocalDateTime lastLogin;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
