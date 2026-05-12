package com.yeoljeong.tripmate.notification.presentation.dto.response;

import com.yeoljeong.tripmate.notification.application.dto.result.NotificationUpdateReadStatusResult;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationReadUpdateStatusResponse(
    UUID notification_history_id,
    boolean isRead,
    LocalDateTime updateAt
) {

  public static NotificationReadUpdateStatusResponse from(
      NotificationUpdateReadStatusResult result) {
    return NotificationReadUpdateStatusResponse.builder()
        .notification_history_id(result.notification_history_id())
        .isRead(result.isRead())
        .updateAt(result.updateAt()).build();
  }
}
