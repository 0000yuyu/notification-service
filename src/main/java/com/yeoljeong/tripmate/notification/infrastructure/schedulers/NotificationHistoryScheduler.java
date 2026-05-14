package com.yeoljeong.tripmate.notification.infrastructure.schedulers;

import com.yeoljeong.tripmate.notification.application.service.command.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class NotificationHistoryScheduler {

  private final NotificationCommandService notificationCommandService;

  @Transactional
  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매일 자정 실행
  public void deleteOldNotifications() {
    notificationCommandService.deleteHistoriesByScheduler();
  }
}
