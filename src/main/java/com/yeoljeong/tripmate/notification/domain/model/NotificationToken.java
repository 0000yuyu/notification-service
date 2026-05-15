package com.yeoljeong.tripmate.notification.domain.model;

import com.yeoljeong.tripmate.domain.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "p_notification_token",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_notification_endpoint",
            columnNames = {"user_id", "channel_type", "device_type", "device_id"}
        ),
        @UniqueConstraint(
            name = "uk_token_value",
            columnNames = {"token_value"}
        )
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationToken extends BaseAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
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

  public String getTokenValue() {
    return notificationEndPoint.getTokenValue();
  }

  public void tokenInActive() {
    this.notificationEndPoint = null;
    this.tokenStatus = TokenStatus.inactiveInitial();
  }

  public void updateTokenStatus(TokenStatus tokenStatus) {
    this.tokenStatus = tokenStatus;
  }

  public void updateTokenEndpoint(NotificationEndPoint endPoint) {
    this.notificationEndPoint = endPoint;
  }
}
