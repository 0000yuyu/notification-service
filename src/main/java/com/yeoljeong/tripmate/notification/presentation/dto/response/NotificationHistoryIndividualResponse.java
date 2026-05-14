package com.yeoljeong.tripmate.notification.presentation.dto.response;

import com.yeoljeong.tripmate.notification.application.dto.result.NotificationHistoryIndividualResult;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationHistoryIndividualResponse(
    NotificationType notificationType,
    UUID id,
    String title,
    String content,
    String redirectUrl,
    Boolean isRead
) {

  public static NotificationHistoryIndividualResponse from(
      NotificationHistoryIndividualResult result) {
    return NotificationHistoryIndividualResponse.builder()
        .notificationType(result.notificationType())
        .id(result.historyId())
        .title(result.title())
        .content(result.content())
        .redirectUrl(result.redirectUrl())
        .isRead(result.isRead()).build();
  }
}
