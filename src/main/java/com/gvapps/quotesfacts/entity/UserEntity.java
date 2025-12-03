package com.gvapps.quotesfacts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
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
    private Boolean isPremium;
    private Boolean darkMode;
    private Boolean notificationEnabled;
    private Integer syncStatus;
    private Integer sessionCount;
    private Integer appOpenCount;
    private Integer appRating;
    private Float engagementScore;
    private Boolean active;

    private LocalDateTime lastLogin;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
