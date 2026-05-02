package com.yeoljeong.tripmate.notification.presentation.dto.request;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSettingCommand;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record NotificationSettingRequest(
    @NotNull
    boolean pushEnabled
) {

  public NotificationSettingCommand toCommand(UUID userId) {
    return NotificationSettingCommand.builder().userId(userId).pushEnabled(pushEnabled).build();
  }
}
