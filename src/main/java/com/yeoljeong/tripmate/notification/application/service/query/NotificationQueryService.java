package com.yeoljeong.tripmate.notification.application.service.query;

import com.yeoljeong.tripmate.notification.application.dto.condition.NotificationHistorySearchCondition;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSettingResult;
import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface NotificationQueryService {

  NotificationSettingResult getSettingData(UUID userId);

  Page<NotificationHistory> getNotificationsByCondition(
      NotificationHistorySearchCondition searchCondition);
}
