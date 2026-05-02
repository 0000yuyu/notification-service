package com.yeoljeong.tripmate.notification.application.service.command.impl;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSettingCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationTokenCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSettingResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationTokenResult;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationCommandService;
import com.yeoljeong.tripmate.notification.domain.model.NotificationEndPoint;
import com.yeoljeong.tripmate.notification.domain.model.NotificationSetting;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import com.yeoljeong.tripmate.notification.domain.model.TokenStatus;
import com.yeoljeong.tripmate.notification.domain.repository.NotificationRepository;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationSettingJpaRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

  private final NotificationRepository notificationRepository;
  private final NotificationSettingJpaRepository notificationSettingJpaRepository;

  @Override
  @Transactional
  public NotificationTokenResult registerTokenData(NotificationTokenCommand command) {

    String deviceId = command.deviceId();
    String tokenValue = command.tokenValue();

    processDuplicateToken(command.userId(), tokenValue);

    NotificationEndPoint newEndPoint =
        NotificationEndPoint.create(command.channelType(),
            command.deviceType(),
            deviceId, tokenValue);

    NotificationToken myTokenData = notificationRepository
        .findTokenDataByUserIdAndChannelTypeAndDeviceIdAndDeviceType(
            command.userId(), command.channelType(), deviceId, command.deviceType())
        .map(tokenData -> {
          tokenData.updateTokenEndpoint(newEndPoint);
          tokenData.updateTokenStatus(TokenStatus.inactiveInitial());
          return tokenData;
        })
        .orElseGet(() -> NotificationToken.create(
            command.userId(),
            newEndPoint,
            TokenStatus.activeInitial()
        ));

    return NotificationTokenResult.from(notificationRepository.saveForTokenData(myTokenData));
  }

  @Override
  public NotificationSettingResult updateSettingData(NotificationSettingCommand command) {
    NotificationSetting settingData = notificationRepository.findSettingDataById(command.userId())
        .orElse(NotificationSetting.createSetting(command.userId()));
    settingData.updatePushEnabled(command.pushEnabled());
    return NotificationSettingResult.from(notificationRepository.saveForSettingData(settingData));

  }

  private void processDuplicateToken(
      UUID userId,
      String tokenValue) {
    notificationRepository
        .findTokenDataByTokenValue(tokenValue)
        .ifPresent(existingToken -> {
          if (!existingToken.getUserId().equals(userId)) {
            existingToken.updateTokenStatus(TokenStatus.inactiveInitial());
            notificationRepository.saveForTokenData(existingToken);
          }
        });
  }
}
