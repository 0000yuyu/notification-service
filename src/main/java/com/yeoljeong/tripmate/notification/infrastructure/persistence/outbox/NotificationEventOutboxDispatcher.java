package com.yeoljeong.tripmate.notification.infrastructure.persistence.outbox;

import com.yeoljeong.tripmate.domain.constants.OutboxStatus;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendEachCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendTarget;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.PayloadConverter;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationSendService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import com.yeoljeong.tripmate.notification.infrastructure.dto.TemplateMessage;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationTokenJpaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventOutboxDispatcher {

  private final NotificationSendOutboxJpaRepository outboxRepository;
  private final NotificationSendService notificationSendService;
  private final NotificationTokenJpaRepository notificationTokenJpaRepository;
  private final PayloadConverter payloadConverter;
  private final TransactionTemplate transactionTemplate;

  @Scheduled(fixedDelay = 1000)
  @Async
  public void dispatch() {

    List<NotificationSendOutbox> pendingEvents = outboxRepository
        .findTop100ByStatusOrderByCreatedAtAsc(
            OutboxStatus.PENDING
        );
    try {
      processSendMessage(pendingEvents.stream()
              .filter(event -> ChannelType.PUSH.equals(event.getChannelType())).toList(),
          ChannelType.PUSH);
      processSendMessage(pendingEvents.stream()
              .filter(event -> ChannelType.EMAIL.equals(event.getChannelType())).toList(),
          ChannelType.EMAIL);

    } catch (Exception e) {
      log.error("알림 발송 중에 오류가 생겼습니다.", e);
    }
  }

  private void processSendMessage(List<NotificationSendOutbox> outboxes,
      ChannelType channelType) {
    if (outboxes.isEmpty()) {
      return;
    }

    Map<UUID, String> tokenMap = notificationTokenJpaRepository
        .findAllByIdIn(outboxes.stream().map(NotificationSendOutbox::getTokenId).toList())
        .stream()
        .collect(Collectors.toMap(
            NotificationToken::getId,
            NotificationToken::getTokenValue
        ));
    List<NotificationSendOutbox> attemptOutboxes = new ArrayList<>();
    List<NotificationSendTarget> targets = new ArrayList<>();

    for (NotificationSendOutbox outbox : outboxes) {
      String token = tokenMap.get(outbox.getTokenId());
      TemplateMessage message = payloadConverter.deserialize(
          outbox.getPayload(),
          TemplateMessage.class
      );

      if (token == null) {
        outbox.fail("토큰 없음");
        continue;
      }

      attemptOutboxes.add(outbox);
      targets.add(NotificationSendTarget.builder()
          .token(token)
          .title(message.title())
          .body(message.body())
          .build());
    }
    NotificationSendResult result = notificationSendService.sendEach(
        NotificationSendEachCommand.builder().channelType(channelType).targets(targets).build());

    transactionTemplate.executeWithoutResult(status -> {
          for (int i = 0; i < attemptOutboxes.size(); i++) {
            NotificationIndividualResult individualResult = result.results().get(i);
            NotificationSendOutbox outbox = attemptOutboxes.get(i);

            if (individualResult.isSuccess()) {
              outbox.published();
            } else {
              outbox.fail(individualResult.errorMessage());
            }
          }
        }
    );
  }
}
