package com.yeoljeong.tripmate.notification.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.event.MatchingMatchedEvent;
import com.yeoljeong.tripmate.event.enums.MatchingTopic;
import com.yeoljeong.tripmate.notification.application.dto.command.EventProcessCommand;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationEventProcessService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import com.yeoljeong.tripmate.notification.infrastructure.config.kafka.KafkaPayloadDeserializer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchingMatchedEventListener {

  private static final Logger log = LogManager.getLogger(MatchingMatchedEventListener.class);
  private final ObjectMapper objectMapper;
  private final NotificationEventProcessService notificationEventProcessService;
  private final KafkaPayloadDeserializer kafkaPayloadDeserializer;

  @KafkaListener
      (
          topics = MatchingTopic.MATCHING_MATCHED_TOPIC,
          groupId = "notification-group",
          containerFactory = "kafkaListenerContainerFactory"
      )
  public void create(@Payload String payload, Acknowledgment ack) {
    MatchingMatchedEvent event = kafkaPayloadDeserializer.deserialize(payload,
        MatchingMatchedEvent.class);
    try {
      notificationEventProcessService.process(
          EventProcessCommand.builder()
              .userList(List.of(event.hostUserId()))
              .channelType(ChannelType.PUSH)
              .topicName(MatchingTopic.MATCHING_MATCHED_TOPIC)
              .notificationType(NotificationType.MATCHING_SUCCEED)
              .refId(event.hostUserId())
              .eventHash(event.eventHash())
              .payload(objectMapper.valueToTree(event)).build());
      ack.acknowledge();
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }
}
