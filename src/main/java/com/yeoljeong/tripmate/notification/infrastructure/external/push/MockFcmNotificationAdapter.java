package com.yeoljeong.tripmate.notification.infrastructure.external.push;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendEachCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendMulticastCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.NotificationSender;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MockFcmNotificationAdapter implements NotificationSender {

  private static final long MIN_DELAY_MS = 450;
  private static final long MAX_DELAY_MS = 550;

  @Override
  public NotificationSendResult sendEach(NotificationSendEachCommand command) {

    simulateExternalDelay();

    List<NotificationIndividualResult> results =
        IntStream.range(0, command.targets().size())
            .mapToObj(NotificationIndividualResult::success)
            .toList();

    return NotificationSendResult.from(results);
  }

  @Override
  public NotificationSendResult sendMulticast(
      NotificationSendMulticastCommand command
  ) {

    simulateExternalDelay();

    log.info("mock 발송 !!");
    List<NotificationIndividualResult> results =
        IntStream.range(0, command.targetTokens().size())
            .mapToObj(NotificationIndividualResult::success)
            .toList();

    return NotificationSendResult.from(results);
  }

  @Override
  public boolean supports(ChannelType type) {
    return type == ChannelType.PUSH;
  }

  private void simulateExternalDelay() {
    long delay =
        ThreadLocalRandom.current()
            .nextLong(MIN_DELAY_MS, MAX_DELAY_MS + 1);

    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Mock FCM interrupted", e);
    }
  }
}