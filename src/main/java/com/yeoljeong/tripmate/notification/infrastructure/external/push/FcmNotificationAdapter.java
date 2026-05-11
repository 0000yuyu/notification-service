package com.yeoljeong.tripmate.notification.infrastructure.external.push;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.NotificationSender;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.exception.NotificationSendErrorCode;
import com.yeoljeong.tripmate.notification.infrastructure.dto.NotificationSendMessage;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcmNotificationAdapter implements NotificationSender {

  private MulticastMessage createMessage(NotificationSendMessage message) {
    return MulticastMessage.builder()
        .addAllTokens(message.targetTokens())
        .setNotification(
            Notification
                .builder()
                .setTitle(message.title())
                .setBody(message.body())
                .build()
        ).build();
  }

  private BatchResponse sendPushMessageForMulticast(MulticastMessage message)
      throws FirebaseMessagingException {
    return FirebaseMessaging.getInstance().sendEachForMulticast(message);
  }

  @Override
  public NotificationSendResult send(NotificationSendMessage sendMessage) {
    MulticastMessage message = createMessage(sendMessage);
    if (sendMessage.targetTokens() != null) {
      try {
        BatchResponse response = sendPushMessageForMulticast(message);
        List<SendResponse> responses = response.getResponses();

        List<NotificationIndividualResult> details = IntStream.range(0, responses.size())
            .mapToObj(index -> {
              SendResponse res = responses.get(index);
              return res.isSuccessful()
                  ? NotificationIndividualResult.success(index)
                  : NotificationIndividualResult.fail(index, res.getException().getMessage());
            })
            .toList();

        return NotificationSendResult.from(details);
      } catch (FirebaseMessagingException e) {
        handleFcmException(e);
      }
    }
    return null;
  }

  @Override
  public boolean supports(ChannelType type) {
    return type == ChannelType.PUSH;
  }

  private void handleFcmException(FirebaseMessagingException e) {
    MessagingErrorCode fcmCode = e.getMessagingErrorCode();
    NotificationSendErrorCode targetError = switch (fcmCode) {
      case UNREGISTERED -> NotificationSendErrorCode.EXPIRED_TOKEN;
      case QUOTA_EXCEEDED -> NotificationSendErrorCode.RATE_LIMIT_EXCEEDED;
      case INTERNAL, UNAVAILABLE -> NotificationSendErrorCode.FCM_SERVER_ERROR;
      case INVALID_ARGUMENT -> NotificationSendErrorCode.INVALID_NOTIFICATION_FORMAT;
      default -> NotificationSendErrorCode.INTERNAL_NOTIFICATION_ERROR;
    };
    throw new BusinessException(targetError);
  }
}
