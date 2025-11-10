package com.gvapps.quotesfacts.repository;

import com.gvapps.quotesfacts.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByDeviceId(String deviceId);

    // 🟢 Update notificationEnabled + fcmToken based on id and appName
    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u " +
            "SET u.notificationEnabled = :notificationEnabled, " +
            "    u.fcmToken = :fcmToken, " +
            "    u.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE u.id = :id AND u.appName = :appName")
    int updateNotificationAndFcmTokenByIdAndAppName(
            @Param("id") Long id,
            @Param("appName") String appName,
            @Param("notificationEnabled") Boolean notificationEnabled,
            @Param("fcmToken") String fcmToken
    );
}
