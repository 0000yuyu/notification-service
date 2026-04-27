package com.yeoljeong.tripmate.notification.domain.entity;

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
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_notification_history")
public class NotificationHistory extends BaseAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @UuidGenerator
  private UUID id;

  @Embedded
  private NotificationEndPoint notificationEndPointSnapShot;

  @Embedded
  private NotificationSource notificationSource;

  @Embedded
  private NotificationPayload notificationPayload;

  @Embedded
  private NotificationResult notificationResult;

  public void updateResult(NotificationResult notificationResult) {
    this.notificationResult = notificationResult;
  }

  public boolean isFailed() {
    return notificationResult.isFailed();
  }

  public NotificationType getNotificationType() {
    return this.notificationSource.getType();
  }

  public ChannelType getNotificationChannelType() {
    return this.notificationEndPointSnapShot.getChannelType();
  }
}
