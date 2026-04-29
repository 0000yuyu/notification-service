package com.yeoljeong.tripmate.notification.application.dto.command;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.DeviceType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationTokenCommand(
    UUID userId,
    ChannelType channelType,
    DeviceType deviceType,
    String deviceId,
    String tokenValue
) {

}
