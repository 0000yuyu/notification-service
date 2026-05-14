package com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.notification.domain.model.NotificationSetting;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingJpaRepository extends JpaRepository<NotificationSetting, UUID> {

  void deleteById(UUID id);
}