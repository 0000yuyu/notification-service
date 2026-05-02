package com.yeoljeong.tripmate.notification.domain.repository;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.DeviceType;
import com.yeoljeong.tripmate.notification.domain.model.NotificationSetting;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {

  Optional<NotificationToken> findTokenDataByTokenValue(String notificationEndPoint_tokenValue);

  Optional<NotificationToken> findTokenDataByUserIdAndChannelTypeAndDeviceIdAndDeviceType(
      UUID userId, ChannelType notificationEndPoint_channelType,
      String notificationEndPoint_deviceId, DeviceType notificationEndPoint_deviceType);

  NotificationToken saveForTokenData(NotificationToken myTokenData);

  Optional<NotificationSetting> findSettingDataById(UUID userId);

  NotificationSetting saveForSettingData(NotificationSetting setting);
}
