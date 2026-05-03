package com.yeoljeong.tripmate.notification.presentation.dto.request;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationAdminSendRequestCommand;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.hibernate.validator.constraints.Length;

public record NotificationAdminSendRequest(
    @NotNull
    List<UUID> userList,
    @NotNull
    ChannelType channelType,
    @NotNull
    @Length(max = 20)
    String title,
    @Length(max = 255)
    @NotNull
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
