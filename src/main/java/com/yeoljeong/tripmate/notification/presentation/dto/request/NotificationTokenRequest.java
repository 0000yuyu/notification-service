package com.yeoljeong.tripmate.notification.presentation.dto.request;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationTokenCommand;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.DeviceType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationTokenRequest(
    @NotNull
    ChannelType channelType,

    @NotNull
    DeviceType deviceType,

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
