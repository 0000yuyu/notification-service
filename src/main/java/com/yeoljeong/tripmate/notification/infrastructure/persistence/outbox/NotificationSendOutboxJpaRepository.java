package com.yeoljeong.tripmate.notification.infrastructure.persistence.outbox;

import com.yeoljeong.tripmate.domain.constants.OutboxStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSendOutboxJpaRepository extends
    JpaRepository<NotificationSendOutbox, UUID> {

  List<NotificationSendOutbox> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus outboxStatus);
}