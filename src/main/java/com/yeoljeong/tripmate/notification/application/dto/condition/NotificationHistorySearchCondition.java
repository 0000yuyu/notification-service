package com.yeoljeong.tripmate.notification.application.dto.condition;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationResultStatus;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import java.util.UUID;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public record NotificationHistorySearchCondition(
    UUID userId,
    NotificationResultStatus status,
    ChannelType channelType,
    NotificationType notificationType,
    Boolean isRead,
    Pageable pageable
) {

}
