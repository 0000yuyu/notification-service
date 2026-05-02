package com.yeoljeong.tripmate.notification.application.dto.result;

import com.yeoljeong.tripmate.notification.domain.model.NotificationSetting;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationSettingResult(
    UUID userId,
    boolean pushEnabled
) {

  public static NotificationSettingResult from(NotificationSetting setting) {
    return NotificationSettingResult.builder()
        .userId(setting.getId())
        .pushEnabled(setting.isPushEnabled()).build();
  }
}
