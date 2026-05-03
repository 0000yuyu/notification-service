package com.yeoljeong.tripmate.notification.application.service.command;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationAdminSendRequestCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSettingCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationTokenCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSettingResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationTokenResult;

public interface NotificationCommandService {

  NotificationTokenResult registerTokenData(NotificationTokenCommand command);

  NotificationSettingResult updateSettingData(NotificationSettingCommand command);

  NotificationSendResult sendNotificationByAdmin(
      NotificationAdminSendRequestCommand requestCommand
  );
}
