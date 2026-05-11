package com.yeoljeong.tripmate.notification.application.provider;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendEachCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendMulticastCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;

public interface NotificationSender {

  NotificationSendResult sendMulticast(NotificationSendMulticastCommand command);

  NotificationSendResult sendEach(NotificationSendEachCommand command);

  boolean supports(ChannelType type);
}
