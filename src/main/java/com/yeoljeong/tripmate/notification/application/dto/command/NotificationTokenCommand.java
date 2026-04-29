package com.yeoljeong.tripmate.notification.application.dto.command;

import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationTokenCommand(
    UUID userId,
    String channelType,
    String deviceType,
    String deviceId,
    String tokenValue
) {

}
