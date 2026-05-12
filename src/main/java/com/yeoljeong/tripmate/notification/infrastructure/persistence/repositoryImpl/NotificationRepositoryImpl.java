package com.yeoljeong.tripmate.notification.infrastructure.persistence.repositoryImpl;

import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.DeviceType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import com.yeoljeong.tripmate.notification.domain.constants.TokenActiveStatus;
import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import com.yeoljeong.tripmate.notification.domain.model.NotificationSetting;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import com.yeoljeong.tripmate.notification.domain.repository.NotificationRepository;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationHistoryJpaRepository;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationSettingJpaRepository;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationTokenJpaRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

  private final NotificationTokenJpaRepository notificationTokenJpaRepository;
  private final NotificationSettingJpaRepository notificationSettingJpaRepository;
  private final NotificationHistoryJpaRepository notificationHistoryJpaRepository;

  @Override
  public Optional<NotificationToken> findTokenDataByTokenValue(String tokenValue) {
    return notificationTokenJpaRepository.findByNotificationEndPoint_TokenValue(tokenValue);
  }

  @Override
  public Optional<NotificationToken> findTokenDataByUserIdAndChannelTypeAndDeviceIdAndDeviceType
      (UUID userId, ChannelType channelType, String deviceId, DeviceType deviceType) {
    return notificationTokenJpaRepository
        .findByUserIdAndNotificationEndPoint_ChannelTypeAndNotificationEndPoint_DeviceIdAndNotificationEndPoint_DeviceType(
            userId, channelType, deviceId, deviceType);
  }

  @Override
  public NotificationToken saveForTokenData(NotificationToken tokenData) {
    return notificationTokenJpaRepository.save(tokenData);
  }

  @Override
  public Optional<NotificationSetting> findSettingDataById(UUID userId) {
    return notificationSettingJpaRepository.findById(userId);
  }

  @Override
  public NotificationSetting saveForSettingData(NotificationSetting settingData) {
    return notificationSettingJpaRepository.save(settingData);
  }

  @Override
  public Page<NotificationHistory> getNotificationsByCondition(
      UUID userId,
      ChannelType channelType,
      NotificationType notificationType,
      Boolean isRead,
      Pageable pageable
  ) {
    Specification<NotificationHistory> specification = ((root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
      if (channelType != null) {
        predicates.add(
            criteriaBuilder.equal(root.get("notificationSource").get("channelType"),
                channelType));
      }
      if (notificationType != null) {
        predicates.add(criteriaBuilder.equal(root.get("notificationSource").get("type"),
            notificationType));
      }
      if (isRead != null) {
        predicates.add(criteriaBuilder.equal(root.get("isRead"), isRead));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    });
    return notificationHistoryJpaRepository.findAll(specification, pageable);
  }

  @Override
  public List<NotificationToken> findSendableTokens(List<UUID> userIds, ChannelType channelType,
      TokenActiveStatus activeStatus) {
    if (userIds == null || userIds.isEmpty()) {
      return List.of();
    }
    return notificationTokenJpaRepository.findSendableTokens(userIds, channelType, activeStatus);
  }

  @Override
  public List<NotificationHistory> saveAllForHistoryData(List<NotificationHistory> histories) {
    return notificationHistoryJpaRepository.saveAll(histories);
  }

  @Override
  public Optional<NotificationHistory> findHistoryDataById(UUID notificationHistoryId) {
    return notificationHistoryJpaRepository.findById(notificationHistoryId);
  }

  @Override
  public NotificationHistory saveForHistoryData(NotificationHistory history) {
    return notificationHistoryJpaRepository.save(history);
  }
}
