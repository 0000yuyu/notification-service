package com.yeoljeong.tripmate.notification.application.dto.command;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationOutboxCommand(
    String topic,
    UUID historyId,
    UUID tokenId,
    ChannelType channelType,
    NotificationType notificationType,
    String message
) {

  public static NotificationOutboxCommand of(String topic, UUID historyId,
      NotificationType notificationType,
      ChannelType channelType, UUID tokenId, String message) {
    return NotificationOutboxCommand.builder()
        .topic(topic)
        .channelType(channelType)
        .notificationType(notificationType)
        .tokenId(tokenId)
        .historyId(historyId)
        .message(message).build();
  }
}
