package com.yeoljeong.tripmate.notification.application.dto.command;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import java.util.List;
import lombok.Builder;

@Builder
public record NotificationSendMulticastCommand(
    List<String> targetTokens,
    ChannelType channelType,
    String title,
    String body
) {

}
