package com.yeoljeong.tripmate.notification.infrastructure.persistence.outbox;

import com.yeoljeong.tripmate.domain.Outbox;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationOutboxCommand;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationResultStatus;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "p_notification_send_outbox")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSendOutbox extends Outbox {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private UUID historyId;

  @Column(nullable = false)
  private UUID tokenId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private NotificationType notificationType;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ChannelType channelType;

  private int retryCount;
  private String reason;
  private int maxRetryCount;
  private LocalDateTime nextAttemptAt;

  @Enumerated(EnumType.STRING)
  private NotificationResultStatus notificationResultStatus;

  public static NotificationSendOutbox from
      (NotificationOutboxCommand command) {
    NotificationSendOutbox outbox = new NotificationSendOutbox();
    init(outbox, command.topic(), command.message());
    outbox.retryCount = 0;
    outbox.historyId = command.historyId();
    outbox.tokenId = command.tokenId();
    outbox.channelType = command.channelType();
    outbox.notificationType = command.notificationType();
    outbox.nextAttemptAt = LocalDateTime.now();
    outbox.maxRetryCount = command.maxRetryCount();
    outbox.notificationResultStatus = NotificationResultStatus.PENDING;
    return outbox;
  }

  public void skip(String skipReason) {
    this.notificationResultStatus = NotificationResultStatus.SKIPPED;
    this.reason = skipReason;
  }

  public void published() {
    super.published();
    this.notificationResultStatus = NotificationResultStatus.PUBLISHED;
  }

  public void fail(String failReason) {
    ++this.retryCount;
    this.reason = failReason;
    this.notificationResultStatus = NotificationResultStatus.FAILED;
    this.nextAttemptAt = LocalDateTime.now().plusMinutes((long) Math.pow(retryCount, 2));
    if (this.retryCount >= maxRetryCount) {
      this.notificationResultStatus = NotificationResultStatus.GIVE_UP;
    }
  }
}
