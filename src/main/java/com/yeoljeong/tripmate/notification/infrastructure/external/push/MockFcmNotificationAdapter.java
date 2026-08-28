package com.yeoljeong.tripmate.notification.infrastructure.external.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.NotificationSender;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.infrastructure.dto.NotificationSendMessage;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test") // 테스트 환경에서만 활성화
@Component
@RequiredArgsConstructor
public class MockFcmNotificationAdapter implements NotificationSender {

  private final ObjectMapper objectMapper;

  // 테스트 시나리오에 따라 동적으로 변경할 수 있는 파라미터
  private long delayMs = 0;          // 지연 시간 (0ms, 500ms, 1000ms, 3000ms)
  private double failureRate = 0.0;    // 실패 확률 (0.0 ~ 1.0)

  @Override
  public NotificationSendResult send(NotificationSendMessage sendMessage) {
    // 1. 지연 시간 주입 (FCM 네트워크 지연 시뮬레이션)
//    if (delayMs > 0) {
//      try {
//        Thread.sleep(delayMs);
//      } catch (InterruptedException e) {
//        Thread.currentThread().interrupt();
//      }
//    }
//
//    // 2. 확률적 실패 주입 (장애 및 에러율 시뮬레이션: 1%, 10%, 30% 등)
//    if (failureRate > 0.0 && Math.random() < failureRate) {
//      throw new RuntimeException("Simulated FCM External API Error");
//    }

    // 3. 대상 토큰이 존재할 경우 원본과 동일하게 성공 결과 구조로 매핑하여 리턴
    if (sendMessage.targetTokens() != null && !sendMessage.targetTokens().isEmpty()) {
      List<NotificationIndividualResult> details = IntStream.range(0,
              sendMessage.targetTokens().size())
          .mapToObj(NotificationIndividualResult::success)
          .toList();
      return NotificationSendResult.from(details);
    }

    return null;
  }

  @Override
  public boolean supports(ChannelType type) {
    return type == ChannelType.PUSH;
  }

  public void updateConditions(long delayMs, double failureRate) {
    this.delayMs = delayMs;
    this.failureRate = failureRate;
  }
}