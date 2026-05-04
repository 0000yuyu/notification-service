package com.yeoljeong.tripmate.notification.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.event.MatchingCandidatesFoundEvent;
import com.yeoljeong.tripmate.event.enums.MatchingTopic;
import com.yeoljeong.tripmate.notification.application.dto.command.EventProcessCommand;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationEventProcessService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchingCandidatedEventListener {

  private static final Logger log = LogManager.getLogger(MatchingCandidatedEventListener.class);
  private final ObjectMapper objectMapper;
  private final NotificationEventProcessService notificationEventProcessService;

  @KafkaListener
      (
          topics = MatchingTopic.MATCHING_CANDIDATES_FOUND_TOPIC,
          groupId = "notification-group",
          containerFactory = "kafkaListenerContainerFactory"
      )
  public void create(MatchingCandidatesFoundEvent event, Acknowledgment ack) {
    try {
      notificationEventProcessService.process(
          EventProcessCommand.builder()
              .userList(event.userIds())
              .channelType(ChannelType.PUSH)
              .topicName(MatchingTopic.MATCHING_CANDIDATES_FOUND_TOPIC)
              .notificationType(NotificationType.MATCHING_CREATED)
              .refId(event.hostUserId())
              .eventHash(event.eventHash())
              .payload(objectMapper.valueToTree(event)).build());
      ack.acknowledge();
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }
}
