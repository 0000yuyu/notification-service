package com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.notification.domain.model.NotificationSendOutbox;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSendOutboxJpaRepository extends
    JpaRepository<NotificationSendOutbox, UUID> {

}