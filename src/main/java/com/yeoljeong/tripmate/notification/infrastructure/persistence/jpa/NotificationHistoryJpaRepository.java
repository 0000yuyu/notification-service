package com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationHistoryJpaRepository extends JpaRepository<NotificationHistory, UUID>,
    JpaSpecificationExecutor<NotificationHistory> {

  @Query("""
          select count(h) > 0
          from NotificationHistory h
          where h.id = :historyId
            and h.isRead = true
      """)
  boolean isRead(UUID historyId);

  @Modifying(clearAutomatically = true)
  @Query("""
      UPDATE NotificationHistory n
      SET n.isRead = true, n.updatedAt = :now
      WHERE n.userId = :userId AND n.isRead = false
      """)
  void markAllAsReadByUserId(UUID userId, LocalDateTime now);

  List<NotificationHistory> findAllByUserId(UUID userId);
}