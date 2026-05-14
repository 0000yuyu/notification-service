package com.yeoljeong.tripmate.notification.application.service.command;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationAdminSendRequestCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationHistoryCreateCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSettingCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationTokenCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSettingResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationTokenResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationUpdateReadStatusResult;
import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import java.util.List;
import java.util.UUID;

public interface NotificationCommandService {

  NotificationTokenResult registerTokenData(NotificationTokenCommand command);

  NotificationSettingResult updateSettingData(NotificationSettingCommand command);

  NotificationSendResult sendNotificationByAdmin(
      NotificationAdminSendRequestCommand requestCommand
  );

  List<NotificationHistory> createHistories(NotificationHistoryCreateCommand command);

  NotificationUpdateReadStatusResult updateReadStatus(UUID uuid, UUID notificationHistoryId);

  void updateAllReadStatus(UUID uuid);

  void deleteHistoriesByScheduler();
}
