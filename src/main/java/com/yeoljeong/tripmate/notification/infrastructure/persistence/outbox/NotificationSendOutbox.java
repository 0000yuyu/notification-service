package com.yeoljeong.tripmate.notification.infrastructure.persistence.outbox;

import com.yeoljeong.tripmate.domain.Outbox;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationOutboxCommand;
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

  public static NotificationSendOutbox from
      (NotificationOutboxCommand command) {
    NotificationSendOutbox outbox = new NotificationSendOutbox();
    init(outbox, command.topic(), command.message());
    outbox.historyId = command.historyId();
    outbox.tokenId = command.tokenId();
    outbox.channelType = command.channelType();
    outbox.notificationType = command.notificationType();
    return outbox;
  }

  public void fail(String failReason) {
    super.fail();
    this.failReason = failReason;
  }
}
