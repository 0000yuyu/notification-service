package com.yeoljeong.tripmate.notification.domain.repository;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.DeviceType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import com.yeoljeong.tripmate.notification.domain.constants.TokenActiveStatus;
import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import com.yeoljeong.tripmate.notification.domain.model.NotificationSetting;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository {

  Optional<NotificationToken> findTokenDataByTokenValue(String notificationEndPoint_tokenValue);

  Optional<NotificationToken> findTokenDataByUserIdAndChannelTypeAndDeviceIdAndDeviceType(
      UUID userId, ChannelType notificationEndPoint_channelType,
      String notificationEndPoint_deviceId, DeviceType notificationEndPoint_deviceType);

  NotificationToken saveForTokenData(NotificationToken myTokenData);

  Optional<NotificationSetting> findSettingDataById(UUID userId);

  NotificationSetting saveForSettingData(NotificationSetting setting);

  Page<NotificationHistory> getNotificationsByCondition(UUID userId,
      ChannelType channelType, NotificationType notificationType,
      Boolean isRead, Pageable pageable);

  List<NotificationToken> findSendableTokens(
      @Param("userIds") List<UUID> userIds,
      @Param("channelType") ChannelType channelType,
      @Param("activeStatus") TokenActiveStatus activeStatus
  );

  List<NotificationHistory> saveAllForHistoryData(List<NotificationHistory> histories);

  Optional<NotificationHistory> findHistoryDataById(UUID notificationHistoryId);

  void updateReadAllHistoryData(UUID userId, LocalDateTime now);
}
