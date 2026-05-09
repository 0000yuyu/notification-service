package com.yeoljeong.tripmate.notification.infrastructure.persistence;

import com.yeoljeong.tripmate.domain.Outbox;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

  private String failReason;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private NotificationType notificationType;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ChannelType channelType;

  public static NotificationSendOutbox create
      (String topic, UUID historyId, NotificationType notificationType, ChannelType channelType,
          UUID tokenId,
          String payload) {
    NotificationSendOutbox outbox = new NotificationSendOutbox();
    init(outbox, topic, payload);
    outbox.historyId = historyId;
    outbox.tokenId = tokenId;
    outbox.channelType = channelType;
    outbox.notificationType = notificationType;
    return outbox;
  }

  public void fail(String failReason) {
    super.fail();
    this.failReason = failReason;
  }
}
