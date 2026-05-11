package com.yeoljeong.tripmate.notification.infrastructure.persistence.outbox;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationOutboxCommand;
import com.yeoljeong.tripmate.notification.application.port.NotificationOutboxPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationOutboxAdapter implements NotificationOutboxPort {

  private final NotificationSendOutboxJpaRepository notificationSendOutboxJpaRepository;

  @Override
  public void publish(List<NotificationOutboxCommand> commands) {
    List<NotificationSendOutbox> outboxes = commands
        .stream()
        .map(NotificationSendOutbox::from)
        .toList();
    notificationSendOutboxJpaRepository.saveAll(outboxes);
  }
}
