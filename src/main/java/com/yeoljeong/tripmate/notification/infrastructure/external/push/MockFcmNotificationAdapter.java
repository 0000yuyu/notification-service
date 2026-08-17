package com.yeoljeong.tripmate.notification.infrastructure.external.push;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendEachCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendMulticastCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendTarget;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.NotificationSender;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"local", "test"})
public class MockFcmNotificationAdapter implements NotificationSender {

  private static final long MOCK_LATENCY_MS = 0;   // 예: 3000 으로 바꾸면 3초 지연 테스트 가능
  private static final double FAILURE_RATE = 0.0;    // 예: 0.3 으로 바꾸면 30% 실패율 테스트 가능

  @Override
  public NotificationSendResult sendMulticast(NotificationSendMulticastCommand command) {
    if (command.targetTokens() == null || command.targetTokens().isEmpty()) {
      return NotificationSendResult.from(List.of());
    }

    log.info("[MOCK FCM] Sending Multicast to {} tokens. Title: {}",
        command.targetTokens().size(), command.title());

    simulateNetworkDelay();

    List<NotificationIndividualResult> details = IntStream.range(0, command.targetTokens().size())
        .mapToObj(index -> {
          boolean isFailed = Math.random() < FAILURE_RATE;
          if (isFailed) {
            log.warn("[MOCK FCM] Multicast token[{}] failed intentionally.", index);
            return NotificationIndividualResult.fail(index, "Mock FCM Intentional Failure");
          }
          return NotificationIndividualResult.success(index);
        })
        .toList();

    return NotificationSendResult.from(details);
  }

  @Override
  public NotificationSendResult sendEach(NotificationSendEachCommand command) {
    List<NotificationSendTarget> targets = command.targets();
    if (targets == null || targets.isEmpty()) {
      return NotificationSendResult.from(List.of());
    }

    log.info("[MOCK FCM] Sending Each to {} targets.", targets.size());

    simulateNetworkDelay();

    List<NotificationIndividualResult> details = IntStream.range(0, targets.size())
        .mapToObj(index -> {
          boolean isFailed = Math.random() < FAILURE_RATE;
          if (isFailed) {
            log.warn("[MOCK FCM] Mock Each target[{}] failed intentionally.", index);
            return NotificationIndividualResult.fail(index, "Mock FCM Intentional Failure");
          }
          return NotificationIndividualResult.success(index);
        })
        .toList();

    return NotificationSendResult.from(details);
  }

  @Override
  public boolean supports(ChannelType type) {
    return type == ChannelType.PUSH;
  }

  private void simulateNetworkDelay() {
    try {
      if (MOCK_LATENCY_MS > 0) {
        Thread.sleep(MOCK_LATENCY_MS);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}