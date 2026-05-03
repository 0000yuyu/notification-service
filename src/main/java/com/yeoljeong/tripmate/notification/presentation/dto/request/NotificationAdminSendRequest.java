package com.yeoljeong.tripmate.notification.presentation.dto.request;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationAdminSendRequestCommand;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import java.util.List;
import java.util.UUID;

public record NotificationAdminSendRequest(
    List<UUID> userList,
    ChannelType channelType,
    String title,
    String body
) {

  public NotificationAdminSendRequestCommand toCommand() {
    return NotificationAdminSendRequestCommand.builder()
        .userList(userList)
        .channelType(channelType)
        .title(title)
        .body(body).build();
  }
}
