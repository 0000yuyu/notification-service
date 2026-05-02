package com.yeoljeong.tripmate.notification.infrastructure.persistence.repositoryImpl;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.DeviceType;
import com.yeoljeong.tripmate.notification.domain.model.NotificationSetting;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import com.yeoljeong.tripmate.notification.domain.repository.NotificationRepository;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationSettingJpaRepository;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationTokenJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

  private final NotificationTokenJpaRepository notificationTokenJpaRepository;
  private final NotificationSettingJpaRepository notificationSettingJpaRepository;

  @Override
  public Optional<NotificationToken> findTokenDataByTokenValue(String tokenValue) {
    return notificationTokenJpaRepository.findByNotificationEndPoint_TokenValue(tokenValue);
  }

  @Override
  public Optional<NotificationToken> findTokenDataByUserIdAndChannelTypeAndDeviceIdAndDeviceType
      (UUID userId, ChannelType channelType, String deviceId, DeviceType deviceType) {
    return notificationTokenJpaRepository
        .findByUserIdAndNotificationEndPoint_ChannelTypeAndNotificationEndPoint_DeviceIdAndNotificationEndPoint_DeviceType(
            userId, channelType, deviceId, deviceType);
  }

  @Override
  public NotificationToken saveForTokenData(NotificationToken tokenData) {
    return notificationTokenJpaRepository.save(tokenData);
  }

  @Override
  public Optional<NotificationSetting> findSettingDataById(UUID userId) {
    return notificationSettingJpaRepository.findById(userId);
  }

  @Override
  public NotificationSetting saveForSettingData(NotificationSetting settingData) {
    return notificationSettingJpaRepository.save(settingData);
  }
}
