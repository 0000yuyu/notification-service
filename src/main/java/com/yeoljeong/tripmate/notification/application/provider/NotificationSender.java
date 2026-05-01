package com.yeoljeong.tripmate.notification.application.provider;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;

public interface NotificationSender {

  NotificationSendResult send(NotificationSendCommand command);

  boolean supports(ChannelType type);
}
