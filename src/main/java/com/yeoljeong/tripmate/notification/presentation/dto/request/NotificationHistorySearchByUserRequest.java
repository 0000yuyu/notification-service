package com.yeoljeong.tripmate.notification.presentation.dto.request;

import com.yeoljeong.tripmate.notification.application.dto.condition.NotificationHistorySearchCondition;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationResultStatus;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public record NotificationHistorySearchByUserRequest(
    NotificationType notificationType,
    Boolean isRead
) {

  public NotificationHistorySearchCondition toCondition(UUID userId, Pageable pageable) {
    return NotificationHistorySearchCondition.builder()
        .userId(userId)
        .status(NotificationResultStatus.SEND)
        .channelType(ChannelType.PUSH)
        .notificationType(notificationType)
        .isRead(isRead)
        .pageable(pageable)
        .build();
  }
}
