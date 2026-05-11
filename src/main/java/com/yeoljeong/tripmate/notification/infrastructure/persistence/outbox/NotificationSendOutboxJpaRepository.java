package com.yeoljeong.tripmate.notification.infrastructure.persistence.outbox;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSendOutboxJpaRepository extends
    JpaRepository<NotificationSendOutbox, UUID> {

}