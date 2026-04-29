package com.yeoljeong.tripmate.notification.application.service.command;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationTokenCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationTokenResult;

public interface NotificationCommandService {

  NotificationTokenResult registerTokenData(NotificationTokenCommand command);

}
