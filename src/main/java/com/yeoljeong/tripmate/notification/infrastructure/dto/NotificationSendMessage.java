package com.yeoljeong.tripmate.notification.infrastructure.dto;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import java.util.List;
import lombok.Builder;

@Builder
public record NotificationSendMessage(
    ChannelType channelType,
    List<String> targetTokens,
    String title,
    String body,
    String redirectUrl
) {

}
