package com.yeoljeong.tripmate.notification.application.dto.command;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import java.util.List;
import lombok.Builder;

@Builder
public record NotificationSendCommand(
    List<String> tokens,
    ChannelType channelType,
    String title,
    String body
) {

}
