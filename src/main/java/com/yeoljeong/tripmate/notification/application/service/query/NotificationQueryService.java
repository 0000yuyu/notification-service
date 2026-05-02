package com.yeoljeong.tripmate.notification.application.service.query;

import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSettingResult;
import java.util.UUID;

public interface NotificationQueryService {

  NotificationSettingResult getSettingData(UUID userId);
}
