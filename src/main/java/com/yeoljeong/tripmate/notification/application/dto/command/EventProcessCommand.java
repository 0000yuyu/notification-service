package com.yeoljeong.tripmate.notification.application.dto.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record EventProcessCommand(
    List<UUID> userList,
    ChannelType channelType,
    UUID refId,
    String eventHash,
    String redirectUrl,
    String topicName,
    NotificationType notificationType,
    JsonNode payload
) {

}
