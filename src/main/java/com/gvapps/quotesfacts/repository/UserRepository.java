package com.gvapps.quotesfacts.repository;

import com.gvapps.quotesfacts.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUniqueId(String uniqueId);

    // 🟢 Update notificationEnabled + fcmToken based on id and appId
    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u " +
            "SET u.notificationEnabled = :notificationEnabled, " +
            "    u.fcmToken = :fcmToken, " +
            "    u.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE u.id = :id AND u.appId = :appId")
    int updateNotificationAndFcmTokenByIdAndAppId(
            @Param("id") Long id,
            @Param("appId") String appId,
            @Param("notificationEnabled") Boolean notificationEnabled,
            @Param("fcmToken") String fcmToken
    );
}
