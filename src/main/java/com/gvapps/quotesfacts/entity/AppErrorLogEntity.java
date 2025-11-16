package com.gvapps.quotesfacts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "error_logs")
public class AppErrorLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "device_os", nullable = false)
    private String deviceOs;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "payload", columnDefinition = "json")
    private String payload;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}
