package com.yeoljeong.tripmate.notification.domain.model;

import com.yeoljeong.tripmate.domain.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_notification_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationHistory extends BaseAuditEntity {


  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private UUID userId;
  @Embedded
  private NotificationSource notificationSource;
  @Embedded
  private NotificationMessage notificationMessage;
  @Embedded
  private NotificationPayload notificationPayload;

  @Column(nullable = false)
  private boolean isRead;

  private NotificationHistory(UUID userId, NotificationSource source,
      NotificationMessage message, NotificationPayload payload) {
    this.userId = userId;
    this.notificationSource = source;
    this.notificationMessage = message;
    this.notificationPayload = payload;
    this.isRead = false;
  }

  public static NotificationHistory create(UUID userId,
      NotificationSource source,
      NotificationMessage message, NotificationPayload payload) {
    return new NotificationHistory(userId, source, message, payload);
  }

  public void markAsRead() {
    this.isRead = true;
  }
}
