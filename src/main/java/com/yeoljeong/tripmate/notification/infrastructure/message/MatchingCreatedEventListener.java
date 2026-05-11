package com.yeoljeong.tripmate.notification.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.event.MatchingCreateEvent;
import com.yeoljeong.tripmate.event.enums.MatchingTopic;
import com.yeoljeong.tripmate.notification.application.dto.command.EventProcessCommand;
import com.yeoljeong.tripmate.notification.application.provider.PayloadConverter;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationEventProcessService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
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
public class MatchingCreatedEventListener {

  private static final Logger log = LogManager.getLogger(MatchingCreatedEventListener.class);
  private final ObjectMapper objectMapper;
  private final NotificationEventProcessService notificationEventProcessService;
  private final PayloadConverter payloadConverter;

  @KafkaListener
      (
          topics = MatchingTopic.MATCHING_CREATED_TOPIC,
          containerFactory = "kafkaListenerContainerFactory"
      )
  public void listen(@Payload String payload, Acknowledgment ack) {
    MatchingCreateEvent event = payloadConverter.deserialize(payload,
        MatchingCreateEvent.class);
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
