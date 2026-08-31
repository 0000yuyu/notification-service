package com.yeoljeong.tripmate.notification.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.event.MatchingCandidatesFoundEvent;
import com.yeoljeong.tripmate.event.enums.MatchingTopic;
import com.yeoljeong.tripmate.notification.application.dto.command.EventProcessCommand;
import com.yeoljeong.tripmate.notification.application.provider.PayloadConverter;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationEventProcessService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchingCandidatedEventListener {

  private static final Logger log = LogManager.getLogger(MatchingCandidatedEventListener.class);
  private static final Logger perfLog = LogManager.getLogger("PERF");
  private final ObjectMapper objectMapper;
  private final NotificationEventProcessService notificationEventProcessService;
  private final PayloadConverter payloadConverter;

  @KafkaListener
      (
          topics = MatchingTopic.MATCHING_CANDIDATES_FOUND_TOPIC,
          groupId = "${spring.kafka.consumer.group-id}",
          containerFactory = "kafkaListenerContainerFactory"
      )
  public void create(@Payload String payload, Acknowledgment ack) {
    MatchingCandidatesFoundEvent event = payloadConverter.deserialize(payload,
        MatchingCandidatesFoundEvent.class);

    perfLog.info(
        "eventHash={} stage=CONSUMER_RECEIVED timestamp={}",
        event.eventHash(),
        Instant.now()
    );

    try {
      notificationEventProcessService.processSend(
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
      log.error("이벤트 처리 실패 : {}", e.getMessage());
    }
  }
}
