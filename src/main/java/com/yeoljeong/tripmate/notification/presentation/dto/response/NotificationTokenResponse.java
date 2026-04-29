package com.yeoljeong.tripmate.notification.presentation.dto.response;

import com.yeoljeong.tripmate.notification.application.dto.result.NotificationTokenResult;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationTokenResponse(
    String channelType,
    String deviceType,
    String deviceId,
    String tokenValue,
    LocalDateTime updateAt
) {

  public static NotificationTokenResponse from(NotificationTokenResult result) {
    return NotificationTokenResponse
        .builder()
        .channelType(result.channelType())
        .deviceType(result.deviceType())
        .deviceId(result.deviceId())
        .tokenValue(result.tokenValue())
        .updateAt(result.updateAt())
        .build();
  }
}
