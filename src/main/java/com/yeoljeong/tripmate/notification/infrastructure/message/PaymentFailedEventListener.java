package com.yeoljeong.tripmate.notification.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.event.PaymentFailedEvent;
import com.yeoljeong.tripmate.event.enums.PaymentTopic;
import com.yeoljeong.tripmate.notification.application.dto.command.EventProcessCommand;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationEventProcessService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFailedEventListener {

  private final ObjectMapper objectMapper;
  private final NotificationEventProcessService notificationEventProcessService;

  @KafkaListener
      (
          topics = PaymentTopic.PAYMENT_FAILED_TOPIC,
          groupId = "notification-group",
          containerFactory = "kafkaListenerContainerFactory"
      )
  public void listen(PaymentFailedEvent event, Acknowledgment ack) {
    try {
      notificationEventProcessService.process(
          EventProcessCommand.builder()
              .userList(List.of(event.userId()))
              .channelType(ChannelType.PUSH)
              .topicName(PaymentTopic.PAYMENT_FAILED_TOPIC)
              .notificationType(NotificationType.PAYMENT_FAILED)
              .refId(event.paymentId())
              .eventHash(event.eventHash())
              .payload(objectMapper.valueToTree(event)).build());

      notificationEventProcessService.process(
          EventProcessCommand.builder()
              .userList(List.of(event.userId()))
              .channelType(ChannelType.EMAIL)
              .refId(event.paymentId())
              .topicName(PaymentTopic.PAYMENT_FAILED_TOPIC)
              .notificationType(NotificationType.PAYMENT_FAILED)
              .eventHash(event.eventHash())
              .payload(objectMapper.valueToTree(event)).build());

      ack.acknowledge();
    } catch (Exception ignored) {
    }
  }
}
