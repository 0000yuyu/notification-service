package com.yeoljeong.tripmate.notification.application.provider;

import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.infrastructure.dto.NotificationSendMessage;

public interface NotificationSender {

  NotificationSendResult send(NotificationSendMessage message);

  boolean supports(ChannelType type);
}
