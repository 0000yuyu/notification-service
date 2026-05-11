package com.yeoljeong.tripmate.notification.application.dto.command;

import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationSettingCommand(
    UUID userId,
    boolean pushEnabled
) {

}
