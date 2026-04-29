package com.yeoljeong.tripmate.notification.presentation.dto.request;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationTokenCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationTokenRequest(
    @NotNull
    @Pattern(regexp = "EMAIL|PUSH")
    String channelType,

    @NotNull
    @Pattern(regexp = "IOS|WEB|ANDROID")
    String deviceType,

    String deviceId,

    @NotNull
    String tokenValue) {

  public NotificationTokenCommand toCommand(UUID userId) {
    return NotificationTokenCommand.builder()
        .channelType(channelType)
        .tokenValue(tokenValue)
        .deviceId(deviceId)
        .deviceType(deviceType)
        .userId(userId)
        .build();
  }
}
