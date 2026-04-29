package com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.DeviceType;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTokenJpaRepository extends JpaRepository<NotificationToken, UUID> {

  Optional<NotificationToken> findByNotificationEndPoint_TokenValue
      (String notificationEndPoint_tokenValue);

  Optional<NotificationToken> findByUserIdAndNotificationEndPoint_ChannelTypeAndNotificationEndPoint_DeviceIdAndNotificationEndPoint_DeviceType
      (
          UUID userId,
          ChannelType notificationEndPoint_channelType,
          String notificationEndPoint_deviceId,
          DeviceType notificationEndPoint_deviceType
      );
}