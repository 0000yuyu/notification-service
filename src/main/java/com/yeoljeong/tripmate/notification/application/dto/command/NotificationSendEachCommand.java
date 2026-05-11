package com.yeoljeong.tripmate.notification.application.dto.command;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import java.util.List;
import lombok.Builder;

@Builder
public record NotificationSendEachCommand(
    ChannelType channelType,
    List<NotificationSendTarget> targets
) {

}

