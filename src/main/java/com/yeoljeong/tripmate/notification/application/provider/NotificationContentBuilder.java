package com.yeoljeong.tripmate.notification.application.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.yeoljeong.tripmate.notification.application.dto.result.TemplateMessageResult;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;

public interface NotificationContentBuilder {

  TemplateMessageResult build(String topicName, ChannelType channelType, JsonNode dataNode);
}