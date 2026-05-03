package com.yeoljeong.tripmate.notification.application.dto.command;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationAdminSendRequestCommand(
    List<UUID> userList,
    ChannelType channelType,
    String title,
    String body
) {

}
