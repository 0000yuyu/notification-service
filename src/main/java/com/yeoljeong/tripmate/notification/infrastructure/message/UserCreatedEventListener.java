package com.yeoljeong.tripmate.notification.infrastructure.message;

import com.yeoljeong.tripmate.event.UserCreatedEvent;
import com.yeoljeong.tripmate.event.enums.UserTopic;
import com.yeoljeong.tripmate.notification.domain.model.NotificationSetting;
import com.yeoljeong.tripmate.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreatedEventListener {

  private static final Logger log = LogManager.getLogger(UserCreatedEventListener.class);
  private final NotificationRepository notificationRepository;

  @KafkaListener
      (
          topics = UserTopic.USER_CREATED_TOPIC,
          groupId = "notification-group",
          containerFactory = "kafkaListenerContainerFactory"
      )
  public void create(UserCreatedEvent event, Acknowledgment ack) {
    try {
      notificationRepository.saveForSettingData(NotificationSetting.createSetting(event.userId()));
      ack.acknowledge();
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }
}
