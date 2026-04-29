package com.yeoljeong.tripmate.notification.application.dto.result;

import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationTokenResult(
    String channelType,
    String deviceType,
    String deviceId,
    String tokenValue,
    LocalDateTime updateAt
) {

  public static NotificationTokenResult from(NotificationToken token) {
    return NotificationTokenResult
        .builder()
        .channelType(String.valueOf(token.getNotificationEndPoint().getChannelType()))
        .deviceType(String.valueOf(token.getNotificationEndPoint().getDeviceType()))
        .deviceId(token.getNotificationEndPoint().getDeviceId())
        .tokenValue(token.getNotificationEndPoint().getTokenValue())
        .updateAt(token.getUpdatedAt())
        .build();
  }
}
