package com.yeoljeong.tripmate.notification.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_notification_setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSetting {

  @Id
  @Column(name = "user_id")
  private UUID id;

  @Column(nullable = false, name = "is_push_enabled")
  private boolean pushEnabled;

  private NotificationSetting(UUID userId, boolean pushEnabled) {
    this.id = userId;
    this.pushEnabled = pushEnabled;
  }

  public static NotificationSetting createSetting(UUID user_id) {
    return new NotificationSetting(user_id, true);
  }

  public void updatePushEnabled(boolean pushEnabled) {
    this.pushEnabled = pushEnabled;
  }
}
