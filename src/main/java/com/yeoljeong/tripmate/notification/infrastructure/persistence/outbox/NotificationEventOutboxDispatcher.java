package com.yeoljeong.tripmate.notification.infrastructure.persistence.outbox;

import com.yeoljeong.tripmate.domain.constants.OutboxStatus;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendEachCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendTarget;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.PayloadConverter;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationSendService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import com.yeoljeong.tripmate.notification.infrastructure.dto.TemplateMessage;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationTokenJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventOutboxDispatcher {

  private final NotificationSendOutboxJpaRepository outboxRepository;
  private final NotificationSendService notificationSendService;
  private final NotificationTokenJpaRepository notificationTokenJpaRepository;
  private final PayloadConverter payloadConverter;

  @Scheduled(fixedDelay = 1000)
  @Transactional
  public void dispatch() {

    List<NotificationSendOutbox> pendingEvents = outboxRepository
        .findTop100ByStatusOrderByCreatedAtAsc(
            OutboxStatus.PENDING
        );
    try {
      processSendMessage(pendingEvents.stream()
          .filter(event -> ChannelType.PUSH.equals(event.getChannelType())).toList());
      processSendMessage(pendingEvents.stream()
          .filter(event -> ChannelType.EMAIL.equals(event.getChannelType())).toList());

    } catch (Exception e) {
      log.error("알림 발송 중에 오류가 생겼습니다.", e);
    }
  }

  private void processSendMessage(List<NotificationSendOutbox> outboxes) {
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

    List<NotificationSendTarget> targets = outboxes.stream()
        .map(event -> {
          TemplateMessage message = payloadConverter.deserialize(
              event.getPayload(),
              TemplateMessage.class
          );

          String token = tokenMap.get(event.getTokenId());

          return NotificationSendTarget.builder()
              .token(token)
              .title(message.title())
              .body(message.body())
              .build();
        })
        .toList();

    NotificationSendResult result = notificationSendService.sendEach(
        NotificationSendEachCommand.builder()
            .channelType(ChannelType.PUSH)
            .targets(targets)
            .build());

    result.results().forEach(r -> {
      int index = r.index();
      NotificationSendOutbox outbox = outboxes.get(index);
      if (r.isSuccess()) {
        outbox.published();
      } else {
        outbox.fail(r.errorMessage());
      }
    });
  }
}
