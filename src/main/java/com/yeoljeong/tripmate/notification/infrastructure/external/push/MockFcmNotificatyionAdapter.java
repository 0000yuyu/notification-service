// package com.yeoljeong.tripmate.notification.infrastructure.external.push;

// import com.google.firebase.messaging.BatchResponse;
// import com.google.firebase.messaging.FirebaseMessaging;
// import com.google.firebase.messaging.FirebaseMessagingException;
// import com.google.firebase.messaging.MessagingErrorCode;
// import com.google.firebase.messaging.MulticastMessage;
// import com.google.firebase.messaging.Notification;
// import com.google.firebase.messaging.SendResponse;
// import com.yeoljeong.tripmate.exception.BusinessException;
// import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
// import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
// import com.yeoljeong.tripmate.notification.application.provider.NotificationSender;
// import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
// import com.yeoljeong.tripmate.notification.domain.exception.NotificationSendErrorCode;
// import com.yeoljeong.tripmate.notification.infrastructure.dto.NotificationSendMessage;
// import java.util.List;
// import java.util.stream.IntStream;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// @RequiredArgsConstructor
// @Profile("test") // 테스트 환경에서만 활성화
// @Component
// public class MockFcmNotificationAdapter implements NotificationSender {

//     private final ObjectMapper objectMapper;
    
//     // 테스트 시나리오에 따라 동적으로 변경할 수 있는 파라미터
//     private long delayMs = 0;          // 지연 시간 (0ms, 500ms, 1000ms, 3000ms)
//     private double failureRate = 0.0;    // 실패 확률 (0.0 ~ 1.0)

//     public MockFcmNotificationAdapter(ObjectMapper objectMapper) {
//         this.objectMapper = objectMapper;
//     }

//     @Override
//     public NotificationSendResult send(NotificationSendEachCommand command) {
//         // 1. 지연 시간 주입 (FCM 네트워크 지연 시뮬레이션)
//         if (delayMs > 0) {
//             try {
//                 Thread.sleep(delayMs);
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//             }
//         }

//         // 2. 확률적 실패 주입 (장애 및 에러율 시뮬레이션: 1%, 10%, 30% 등)
//         if (failureRate > 0.0 && Math.random() < failureRate) {
//             throw new RuntimeException("Simulated FCM External API Error");
//         }

//         // 3. 정상 성공 리턴
//         return NotificationSendResult.success();
//     }

//     // 테스트 중에 지연 시간이나 실패율을 동적으로 조절하기 위한 Setter
//     public void updateConditions(long delayMs, double failureRate) {
//         this.delayMs = delayMs;
//         this.failureRate = failureRate;
//     }
// }