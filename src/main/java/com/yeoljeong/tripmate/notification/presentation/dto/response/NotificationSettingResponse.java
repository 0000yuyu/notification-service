package com.yeoljeong.tripmate.notification.presentation.dto.response;

import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSettingResult;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationSettingResponse(
    UUID userId,
    boolean pushEnabled) {

  public static NotificationSettingResponse from(NotificationSettingResult result) {
    return NotificationSettingResponse.builder()
        .userId(result.userId())
        .pushEnabled(result.pushEnabled()).build();
  }
}
