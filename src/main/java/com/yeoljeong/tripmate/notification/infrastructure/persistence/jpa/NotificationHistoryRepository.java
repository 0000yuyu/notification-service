package com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, UUID>,
    JpaSpecificationExecutor<NotificationHistory> {

}