package com.yeoljeong.tripmate.notification.application.dto.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationHistoryCreateCommand(
    List<UUID> userList,
    ChannelType channelType,
    String title,
    String body,
    UUID refId,
    String eventHash,
    NotificationType notificationType,
    JsonNode payload,
    String redirectUrl
) {

  public static NotificationHistoryCreateCommand toHistoryCommand(
      EventProcessCommand processCommand, String title, String body) {
    return NotificationHistoryCreateCommand.builder()
        .userList(processCommand.userList())
        .channelType(processCommand.channelType())
        .title(title)
        .body(body)
        .refId(processCommand.refId())
        .eventHash(processCommand.eventHash())
        .notificationType(processCommand.notificationType())
        .payload(processCommand.payload())
        .redirectUrl(processCommand.redirectUrl())
        .build();
  }
}
