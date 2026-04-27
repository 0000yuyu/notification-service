package com.yeoljeong.tripmate.notification.domain.entity;

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
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_notification_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationToken extends BaseAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @UuidGenerator
  private UUID id;

  @Column(name = "user_id")
  private UUID userId;

  @Embedded
  private NotificationEndPoint notificationEndPoint;

  @Embedded
  private TokenStatus tokenStatus;

  private NotificationToken(UUID userId, NotificationEndPoint notificationEndPoint,
      TokenStatus tokenStatus) {
    this.userId = userId;
    this.notificationEndPoint = notificationEndPoint;
    this.tokenStatus = tokenStatus;
  }

  public static NotificationToken create(UUID userId, NotificationEndPoint notificationEndPoint,
      TokenStatus tokenStatus) {
    return new NotificationToken(userId, notificationEndPoint, tokenStatus);
  }

  public void updateTokenStatus(TokenStatus tokenStatus) {
    this.tokenStatus = tokenStatus;
  }

  public boolean isTokenUsable() {
    return tokenStatus.isUsable();
  }
}
