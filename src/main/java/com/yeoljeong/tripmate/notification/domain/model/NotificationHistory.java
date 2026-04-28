package com.yeoljeong.tripmate.notification.domain.model;

import com.yeoljeong.tripmate.domain.BaseAuditEntity;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_notification_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationHistory extends BaseAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Embedded
  private NotificationEndPoint notificationEndPointSnapShot;

  @Embedded
  private NotificationSource notificationSource;

  @Embedded
  private NotificationMessage notificationMessage;

  @Embedded
  private NotificationPayload notificationPayload;

  @Embedded
  private NotificationResult notificationResult;

  private NotificationHistory(NotificationEndPoint endPoint, NotificationSource source,
      NotificationMessage message, NotificationPayload payload, NotificationResult result) {
    this.notificationEndPointSnapShot = endPoint;
    this.notificationSource = source;
    this.notificationMessage = message;
    this.notificationPayload = payload;
    this.notificationResult = result;
  }

  public static NotificationHistory create(NotificationEndPoint endPoint, NotificationSource source,
      NotificationMessage message, NotificationPayload payload, NotificationResult result) {
    return new NotificationHistory(endPoint, source, message, payload, result);
  }

  public void updateResult(NotificationResult notificationResult) {
    this.notificationResult = notificationResult;
  }

  protected boolean isFailed() {
    return notificationResult.isFailed();
  }

  protected NotificationType getNotificationType() {
    return this.notificationSource.getType();
  }

  protected ChannelType getNotificationChannelType() {
    return this.notificationEndPointSnapShot.getChannelType();
  }
}
