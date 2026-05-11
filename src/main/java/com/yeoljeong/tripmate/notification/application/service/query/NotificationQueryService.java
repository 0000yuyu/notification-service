package com.yeoljeong.tripmate.notification.application.service.query;

import com.yeoljeong.tripmate.notification.application.dto.condition.NotificationHistorySearchCondition;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationHistoryIndividualResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSettingResult;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface NotificationQueryService {

  NotificationSettingResult getSettingData(UUID userId);

  Page<NotificationHistoryIndividualResult> getNotificationsByCondition(
      NotificationHistorySearchCondition searchCondition);
}
