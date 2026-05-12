package com.yeoljeong.tripmate.notification.application.dto.result;

import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationUpdateReadStatusResult(
    UUID notification_history_id,
    boolean isRead,
    LocalDateTime updateAt
) {

  public static NotificationUpdateReadStatusResult from(NotificationHistory history) {
    return NotificationUpdateReadStatusResult.builder()
        .notification_history_id(history.getId())
        .isRead(history.isRead())
        .updateAt(history.getUpdatedAt()).build();
  }
}
