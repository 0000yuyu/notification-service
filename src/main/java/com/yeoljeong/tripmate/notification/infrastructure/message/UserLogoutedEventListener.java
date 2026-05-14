package com.yeoljeong.tripmate.notification.infrastructure.message;

import com.yeoljeong.tripmate.event.UserLogoutEvent;
import com.yeoljeong.tripmate.event.enums.UserTopic;
import com.yeoljeong.tripmate.notification.application.provider.PayloadConverter;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationEventProcessService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLogoutedEventListener {

  private static final Logger log = LogManager.getLogger(UserLogoutedEventListener.class);
  private final PayloadConverter payloadConverter;
  private final NotificationEventProcessService notificationEventProcessService;

  @KafkaListener
      (
          topics = UserTopic.USER_LOGOUT_TOPIC,
          groupId = "${spring.kafka.consumer.group-id}",
          containerFactory = "kafkaListenerContainerFactory"
      )
  public void create(@Payload String payload, Acknowledgment ack) {
    UserLogoutEvent event = payloadConverter.deserialize(payload,
        UserLogoutEvent.class);
    try {
      notificationEventProcessService.processUserLogout(event.userId());
      ack.acknowledge();
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }
}
