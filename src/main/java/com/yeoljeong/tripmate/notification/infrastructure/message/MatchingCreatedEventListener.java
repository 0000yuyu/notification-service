package com.yeoljeong.tripmate.notification.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.event.MatchingCreateEvent;
import com.yeoljeong.tripmate.event.enums.MatchingTopic;
import com.yeoljeong.tripmate.notification.application.dto.command.EventProcessCommand;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationCommandService;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationEventProcessService;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationSendService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationTokenJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchingCreatedEventListener {

  private static final Logger log = LogManager.getLogger(MatchingCreatedEventListener.class);
  private final NotificationTokenJpaRepository notificationTokenJpaRepository;
  private final NotificationSendService notificationSendService;
  private final NotificationCommandService notificationCommandService;
  private final ObjectMapper objectMapper;
  private final NotificationEventProcessService notificationEventProcessService;

  @KafkaListener
      (
          topics = MatchingTopic.MATCHING_CREATED_TOPIC,
          groupId = "notification-group",
          containerFactory = "kafkaListenerContainerFactory"
      )
  public void listen(MatchingCreateEvent event, Acknowledgment ack) {
    try {
      notificationEventProcessService.process(
          EventProcessCommand.builder()
              .userList(List.of(event.hostUserId()))
              .channelType(ChannelType.PUSH)
              .topicName(MatchingTopic.MATCHING_CREATED_TOPIC)
              .notificationType(NotificationType.MATCHING_CREATED)
              .refId(event.matchingId())
              .eventHash(event.eventHash())
              .payload(objectMapper.valueToTree(event)).build());
      ack.acknowledge();
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }
}
