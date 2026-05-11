package com.yeoljeong.tripmate.notification.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.event.PaymentFailedEvent;
import com.yeoljeong.tripmate.event.enums.PaymentTopic;
import com.yeoljeong.tripmate.notification.application.dto.command.EventProcessCommand;
import com.yeoljeong.tripmate.notification.application.provider.PayloadConverter;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationEventProcessService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFailedEventListener {

  private final ObjectMapper objectMapper;
  private final NotificationEventProcessService notificationEventProcessService;
  private final PayloadConverter payloadConverter;

  @KafkaListener
      (
          topics = PaymentTopic.PAYMENT_FAILED_TOPIC,
          groupId = "${spring.kafka.consumer.group-id}",
          containerFactory = "kafkaListenerContainerFactory"
      )
  public void listen(@Payload String payload, Acknowledgment ack) {
    PaymentFailedEvent event = payloadConverter.deserialize(payload,
        PaymentFailedEvent.class);
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
