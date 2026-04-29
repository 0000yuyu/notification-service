package com.yeoljeong.tripmate.notification.application.service.command.impl;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationTokenCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationTokenResult;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationCommandService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.DeviceType;
import com.yeoljeong.tripmate.notification.domain.model.NotificationEndPoint;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import com.yeoljeong.tripmate.notification.domain.model.TokenStatus;
import com.yeoljeong.tripmate.notification.domain.repository.NotificationRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

  private final NotificationRepository notificationRepository;

  @Override
  public NotificationTokenResult registerTokenData(NotificationTokenCommand command) {

    ChannelType channelType = ChannelType.valueOf(command.channelType());
    DeviceType deviceType = DeviceType.valueOf(command.deviceType());
    String deviceId = command.deviceId();
    String tokenValue = command.tokenValue();

    processDuplicateToken(command.userId(), tokenValue);

    NotificationEndPoint newEndPoint = NotificationEndPoint.create(channelType, deviceType,
        deviceId, tokenValue);

    NotificationToken myTokenData = notificationRepository
        .findByUserIdAndChannelTypeAndDeviceIdAndDeviceType(
            command.userId(), channelType, deviceId, deviceType)
        .map(tokenData -> {
          tokenData.updateTokenStatus(TokenStatus.inactiveInitial());
          return tokenData;
        })
        .orElseGet(() -> NotificationToken.create(
            command.userId(),
            newEndPoint,
            TokenStatus.activeInitial()
        ));

    return NotificationTokenResult.from(notificationRepository.save(myTokenData));
  }

  private void processDuplicateToken(
      UUID userId,
      String tokenValue) {
    notificationRepository
        .findByTokenValue(tokenValue)
        .ifPresent(existingToken -> {
          if (!existingToken.getUserId().equals(userId)) {
            existingToken.updateTokenStatus(TokenStatus.inactiveInitial());
            notificationRepository.save(existingToken);
          }
        });
  }
}
