package com.yeoljeong.tripmate.notification.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.event.PlanUnitConfirmedEvent;
import com.yeoljeong.tripmate.event.enums.PlanTopic;
import com.yeoljeong.tripmate.notification.application.dto.command.EventProcessCommand;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationEventProcessService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanConfirmedEventListener {

  private final ObjectMapper objectMapper;
  private final NotificationEventProcessService notificationEventProcessService;

  @KafkaListener
      (
          topics = PlanTopic.PLAN_UNIT_CONFIRMED_TOPIC,
          groupId = "notification-group",
          containerFactory = "kafkaListenerContainerFactory"
      )
  public void listen(PlanUnitConfirmedEvent event, Acknowledgment ack) {
    try {
      notificationEventProcessService.process(
          EventProcessCommand.builder()
              .userList(event.receivers())
              .channelType(ChannelType.PUSH)
              .topicName(PlanTopic.PLAN_UNIT_CONFIRMED_TOPIC)
              .notificationType(NotificationType.PLAN_CONFIRMED)
              .refId(event.planUnitId())
              .eventHash(event.eventHash())
              .payload(objectMapper.valueToTree(event)).build());
      ack.acknowledge();
    } catch (Exception ignored) {
    }
  }
}
