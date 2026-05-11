package com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.DeviceType;
import com.yeoljeong.tripmate.notification.domain.constants.TokenActiveStatus;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

  @Query("""
          select nt
          from NotificationToken nt
          where nt.userId in :userIds
            and nt.notificationEndPoint.channelType = :channelType
            and nt.tokenStatus.activeStatus = :activeStatus
      """)
  List<NotificationToken> findSendableTokens(
      @Param("userIds") List<UUID> userIds,
      @Param("channelType") ChannelType channelType,
      @Param("activeStatus") TokenActiveStatus activeStatus
  );

  List<NotificationToken> findAllByIdIn(List<UUID> id);
}