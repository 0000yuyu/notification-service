package com.yeoljeong.tripmate.notification.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.event.PaymentRefundedEvent;
import com.yeoljeong.tripmate.event.enums.PaymentTopic;
import com.yeoljeong.tripmate.notification.application.dto.command.EventProcessCommand;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationEventProcessService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationType;
import com.yeoljeong.tripmate.notification.infrastructure.config.kafka.KafkaPayloadDeserializer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRefundEventListener {

  private final ObjectMapper objectMapper;
  private final NotificationEventProcessService notificationEventProcessService;
  private final KafkaPayloadDeserializer kafkaPayloadDeserializer;

  @KafkaListener
      (
          topics = PaymentTopic.PAYMENT_REFUNDED_TOPIC,
          containerFactory = "kafkaListenerContainerFactory"
      )
  public void create(@Payload String payload, Acknowledgment ack) {
    PaymentRefundedEvent event = kafkaPayloadDeserializer.deserialize(payload,
        PaymentRefundedEvent.class);
    try {
      notificationEventProcessService.process(
          EventProcessCommand.builder()
              .userList(List.of(event.userId()))
              .channelType(ChannelType.PUSH)
              .topicName(PaymentTopic.PAYMENT_REFUNDED_TOPIC)
              .notificationType(NotificationType.PAYMENT_SUCCEED)
              .refId(event.productId())
              .eventHash(event.eventHash())
              .payload(objectMapper.valueToTree(event)).build());

      notificationEventProcessService.process(
          EventProcessCommand.builder()
              .userList(List.of(event.userId()))
              .channelType(ChannelType.EMAIL)
              .refId(event.productId())
              .topicName(PaymentTopic.PAYMENT_REFUNDED_TOPIC)
              .notificationType(NotificationType.PAYMENT_SUCCEED)
              .eventHash(event.eventHash())
              .payload(objectMapper.valueToTree(event)).build());

      ack.acknowledge();
    } catch (Exception ignored) {
    }
  }
}
