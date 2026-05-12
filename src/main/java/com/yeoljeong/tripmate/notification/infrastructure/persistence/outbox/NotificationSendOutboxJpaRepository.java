package com.yeoljeong.tripmate.notification.infrastructure.persistence.outbox;

import com.yeoljeong.tripmate.notification.domain.constants.NotificationResultStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationSendOutboxJpaRepository extends
    JpaRepository<NotificationSendOutbox, UUID> {

  List<NotificationSendOutbox> findTop100ByNotificationResultStatusInAndNextAttemptAtLessThanEqual(
      List<NotificationResultStatus> notificationResultStatus, LocalDateTime nextAttemptAt);

  @Query("""
          select count(o) > 0
          from NotificationSendOutbox o
          where o.historyId = :historyId
            and o.notificationResultStatus = 'PUBLISHED'
      """)
  boolean existsSuccessByHistoryId(UUID historyId);
}