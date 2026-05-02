package com.yeoljeong.tripmate.notification.application.service.query.impl;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.notification.application.dto.condition.NotificationHistorySearchCondition;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSettingResult;
import com.yeoljeong.tripmate.notification.application.service.query.NotificationQueryService;
import com.yeoljeong.tripmate.notification.domain.exception.NotificationSettingErrorCode;
import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import com.yeoljeong.tripmate.notification.domain.model.NotificationSetting;
import com.yeoljeong.tripmate.notification.domain.repository.NotificationRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {

  private final NotificationRepository notificationRepository;

  public NotificationSettingResult getSettingData(UUID userId) {
    NotificationSetting settingData = notificationRepository.findSettingDataById(userId)
        .orElseThrow(
            () -> new BusinessException(NotificationSettingErrorCode.USER_SETTING_NOT_FOUND));
    return NotificationSettingResult.from(settingData);
  }

  @Override
  public Page<NotificationHistory> getNotificationsByCondition(
      NotificationHistorySearchCondition searchCondition) {
    return notificationRepository.getNotificationsByCondition
        (
            searchCondition.userId(),
            searchCondition.status(),
            searchCondition.channelType(),
            searchCondition.notificationType(),
            searchCondition.isRead(),
            searchCondition.pageable()
        );
  }
}
