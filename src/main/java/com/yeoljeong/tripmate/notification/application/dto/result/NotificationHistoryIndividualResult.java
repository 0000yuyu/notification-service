package com.yeoljeong.tripmate.notification.application.dto.result;

import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationHistoryIndividualResult(
    NotificationType notificationType,
    String title,
    String content,
    String redirectUrl,
    Boolean isRead,
    UUID historyId
) {

  public static NotificationHistoryIndividualResult from(NotificationHistory history) {
    return NotificationHistoryIndividualResult.builder()
        .notificationType(history.getNotificationSource().getType())
        .title(history.getNotificationMessage().getTitle())
        .content(history.getNotificationMessage().getBody())
        .redirectUrl(history.getNotificationMessage().getRedirectUrl())
        .historyId(history.getId())
        .isRead(history.isRead()).build();
  }
}
