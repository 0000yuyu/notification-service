package com.yeoljeong.tripmate.notification.application.service.command.impl;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationAdminSendRequestCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationHistoryCreateCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendMulticastCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSettingCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationTokenCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSettingResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationTokenResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationUpdateReadStatusResult;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationCommandService;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationSendService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.TokenActiveStatus;
import com.yeoljeong.tripmate.notification.domain.exception.NotificationHistoryErrorCode;
import com.yeoljeong.tripmate.notification.domain.exception.NotificationSettingErrorCode;
import com.yeoljeong.tripmate.notification.domain.model.NotificationEndPoint;
import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import com.yeoljeong.tripmate.notification.domain.model.NotificationMessage;
import com.yeoljeong.tripmate.notification.domain.model.NotificationPayload;
import com.yeoljeong.tripmate.notification.domain.model.NotificationSetting;
import com.yeoljeong.tripmate.notification.domain.model.NotificationSource;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import com.yeoljeong.tripmate.notification.domain.model.TokenStatus;
import com.yeoljeong.tripmate.notification.domain.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

  private final NotificationRepository notificationRepository;
  private final NotificationSendService notificationSendService;

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
          tokenData.updateTokenStatus(TokenStatus.activeInitial());
          return tokenData;
        })
        .orElseGet(() -> NotificationToken.create(
            command.userId(),
            newEndPoint,
            TokenStatus.activeInitial()
        ));

    return NotificationTokenResult.from(notificationRepository.saveForTokenData(myTokenData));
  }

  @Transactional
  @Override
  public NotificationSettingResult updateSettingData(NotificationSettingCommand command) {
    NotificationSetting settingData = notificationRepository.findSettingDataById(command.userId())
        .orElseThrow(
            () -> new BusinessException(NotificationSettingErrorCode.USER_SETTING_NOT_FOUND));
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
            notificationRepository.deleteTokenByTokenId(existingToken.getId());
          }
        });
  }

  @Override
  public NotificationSendResult sendNotificationByAdmin(
      NotificationAdminSendRequestCommand requestCommand
  ) {
    List<NotificationToken> tokenEntities = notificationRepository
        .findSendableTokens(
            requestCommand.userList(),
            requestCommand.channelType(),
            TokenActiveStatus.ACTIVE
        );

    List<String> tokens = tokenEntities.stream()
        .map(NotificationToken::getTokenValue)
        .filter(Objects::nonNull)
        .toList();

    return notificationSendService.sendMulticast(
        NotificationSendMulticastCommand.builder()
            .channelType(requestCommand.channelType())
            .targetTokens(tokens)
            .title(requestCommand.title())
            .body(requestCommand.body())
            .build()
    );
  }

  @Override
  public List<NotificationHistory> createHistories(
      NotificationHistoryCreateCommand command) {
    List<NotificationHistory> histories =
        command.userList()
            .stream().map(userId ->
                NotificationHistory
                    .create(
                        userId,
                        NotificationSource.create(
                            command.notificationType(),
                            command.channelType(),
                            command.refId(),
                            command.eventHash()),
                        NotificationMessage.create().title(command.title()).body(command.body())
                            .redirectUrl(command.redirectUrl()).build(),
                        new NotificationPayload(command.payload())
                    )).toList();
    return notificationRepository.saveAllForHistoryData(histories);
  }

  @Transactional
  @Override
  public NotificationUpdateReadStatusResult updateReadStatus(UUID userId,
      UUID notificationHistoryId) {
    NotificationHistory history = notificationRepository.findHistoryDataById(notificationHistoryId)
        .orElseThrow(() -> new BusinessException(NotificationHistoryErrorCode.HISTORY_NOT_FOUND));
    if (!history.getUserId().equals(userId)) {
      throw new BusinessException(NotificationHistoryErrorCode.HISTORY_NOT_ACCESSIBLE);
    }
    history.markAsRead();
    return NotificationUpdateReadStatusResult.from(history);
  }

  @Transactional
  @Override
  public void updateAllReadStatus(UUID userId) {
    notificationRepository.updateReadAllHistoryData(userId, LocalDateTime.now());
  }

  @Transactional
  @Override
  public void deleteHistoriesByScheduler() {
    LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
    notificationRepository.deleteHistoriesByScheduler(ChannelType.PUSH, cutoff);
  }
}