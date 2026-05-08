package com.yeoljeong.tripmate.notification.application.dto.command;

import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationEventCommand {

  private String eventHash;
  private UUID refId;
  private String redirectUrl;
  private NotificationType notificationType;
}