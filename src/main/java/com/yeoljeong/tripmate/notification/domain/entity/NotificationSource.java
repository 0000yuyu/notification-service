package com.yeoljeong.tripmate.notification.domain.entity;

import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSource {

  @Enumerated(EnumType.STRING)
  @Column(name = "notification_type", nullable = false)
  private NotificationType type;

  @Column()
  private UUID refId;

  @Column(nullable = false)
  private String eventHash;

  private NotificationSource(NotificationType type, UUID refId, String eventHash) {
    this.type = type;
    this.refId = refId;
    this.eventHash = eventHash;
  }

  public static NotificationSource create(NotificationType type, UUID refId, String eventHash) {
    return new NotificationSource(type, refId, eventHash);
  }
}
