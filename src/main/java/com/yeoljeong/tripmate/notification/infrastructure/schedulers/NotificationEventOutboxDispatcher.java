package com.yeoljeong.tripmate.notification.infrastructure.schedulers;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendEachCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendTarget;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.PayloadConverter;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationSendService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationResultStatus;
import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import com.yeoljeong.tripmate.notification.infrastructure.dto.TemplateMessage;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationHistoryJpaRepository;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationTokenJpaRepository;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.outbox.NotificationSendOutbox;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.outbox.NotificationSendOutboxJpaRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final NotificationHistoryJpaRepository notificationHistoryJpaRepository;

  @Scheduled(fixedDelay = 1000)
  public void dispatch() {
    List<NotificationSendOutbox> targetOutboxes = getSendableOutboxes();
    log.info("알림 발송 할 항목 : {}", targetOutboxes.size());
    try {
      targetOutboxes.stream()
          .collect(Collectors.groupingBy(NotificationSendOutbox::getChannelType))
          .forEach(this::processSendMessage);
    } catch (Exception e) {
      log.error("알림 발송 중에 오류가 생겼습니다.", e);
    }
  }

  private List<NotificationSendOutbox> getSendableOutboxes() {
    List<NotificationSendOutbox> outboxes =
        outboxRepository.findTop100ByNotificationResultStatusInAndNextAttemptAtLessThanEqual
            (
                List.of(NotificationResultStatus.PENDING, NotificationResultStatus.FAILED),
                LocalDateTime.now()
            );
    log.info("outbox candidate : {}", outboxes.size());

    transactionTemplate.executeWithoutResult(transactionStatus -> {
      outboxes.forEach(outbox -> {
        if (notificationHistoryJpaRepository.isRead(outbox.getHistoryId())) {
          outbox.skip("이미 읽음");
          log.info("Outbox ID: {} -> 이미 읽음 처리", outbox.getId());
        } else if (!notificationTokenJpaRepository.isSendableToken(outbox.getTokenId())) {
          outbox.skip("토큰이 없거나 비활성화됨");
          log.info("Outbox ID: {} -> 토큰 비활성화", outbox.getId());
        } else if (outbox.getNotificationResultStatus() == NotificationResultStatus.FAILED
            && outboxRepository.existsSuccessByHistoryId(outbox.getHistoryId())) {
          outbox.skip("재발송 중 타 기기 발송 성공으로 인한 스킵");
          log.info("Outbox ID: {} -> 타 기기 성공 스킵", outbox.getId());
        }
      });
      outboxRepository.saveAll(outboxes);
    });

    return outboxes.stream()
        .filter(outbox -> outbox.getNotificationResultStatus() != NotificationResultStatus.SKIPPED)
        .toList();
  }

  private void processSendMessage(ChannelType channelType,
      List<NotificationSendOutbox> targetOutboxes) {
    if (targetOutboxes.isEmpty()) {
      return;
    }



    Map<UUID, String> tokenMap = notificationTokenJpaRepository
        .findAllByIdIn(targetOutboxes.stream().map(NotificationSendOutbox::getTokenId).toList())
        .stream()
        .collect(Collectors.toMap(
            NotificationToken::getId,
            NotificationToken::getTokenValue
        ));
    List<NotificationSendTarget> targets = targetOutboxes.stream().map(
        outbox -> {
          TemplateMessage message = payloadConverter.deserialize(outbox.getPayload(),
              TemplateMessage.class);
          return NotificationSendTarget.builder().title(message.title())
              .body(message.body())
              .token(tokenMap.get(outbox.getTokenId()))
              .build();
        }
    ).toList();

    Map<UUID, String> eventHashMap = notificationHistoryJpaRepository
        .findAllById(
            targetOutboxes.stream()
                .map(NotificationSendOutbox::getHistoryId)
                .toList()
        )
        .stream()
        .collect(Collectors.toMap(
            NotificationHistory::getId,
            history -> history.getNotificationSource().getEventHash()
        ));

    Instant sendStartedAt = Instant.now();

    targetOutboxes.forEach(outbox ->
        log.info(
            "[PERF] eventHash={} stage=FCM_SEND_STARTED timestamp={}",
            eventHashMap.get(outbox.getHistoryId()),
            sendStartedAt
        )
    );


    NotificationSendResult result = notificationSendService.sendEach(
        NotificationSendEachCommand.builder().channelType(channelType).targets(targets).build());

    log.info("토큰 발송 결과 : 개수 : {}, {}", result.results().size(), result.results());

    Instant sendFinishedAt = Instant.now();

    for (int i = 0; i < targetOutboxes.size(); i++) {
      NotificationIndividualResult individualResult = result.results().get(i);
      NotificationSendOutbox outbox = targetOutboxes.get(i);

      if (individualResult.isSuccess()) {
        log.info(
            "[PERF] eventHash={} stage=FCM_SEND_SUCCESS timestamp={}",
            eventHashMap.get(outbox.getHistoryId()),
            sendFinishedAt
        );

        outbox.published();

      } else {
        log.info(
            "[PERF] eventHash={} stage=FCM_SEND_FAILED timestamp={}",
            eventHashMap.get(outbox.getHistoryId()),
            sendFinishedAt
        );

        outbox.fail(individualResult.errorMessage());
      }
    }

    transactionTemplate.executeWithoutResult(status -> {
          for (int i = 0; i < targetOutboxes.size(); i++) {
            NotificationIndividualResult individualResult = result.results().get(i);
            NotificationSendOutbox outbox = targetOutboxes.get(i);
            if (individualResult.isSuccess()) {
              outbox.published();
            } else {
              outbox.fail(individualResult.errorMessage());
            }
          }
          outboxRepository.saveAll(targetOutboxes);
        }
    );

  }
}
