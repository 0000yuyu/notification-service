package com.yeoljeong.tripmate.notification.application.service.query.impl;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSettingResult;
import com.yeoljeong.tripmate.notification.application.service.query.NotificationQueryService;
import com.yeoljeong.tripmate.notification.domain.exception.NotificationSettingErrorCode;
import com.yeoljeong.tripmate.notification.domain.model.NotificationSetting;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationSettingJpaRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {

  private final NotificationSettingJpaRepository notificationSettingJpaRepository;

  public NotificationSettingResult getSettingData(UUID userId) {
    NotificationSetting settingData = notificationSettingJpaRepository.findById(userId)
        .orElseThrow(
            () -> new BusinessException(NotificationSettingErrorCode.USER_SETTING_NOT_FOUND));
    return NotificationSettingResult.from(settingData);
  }
}
