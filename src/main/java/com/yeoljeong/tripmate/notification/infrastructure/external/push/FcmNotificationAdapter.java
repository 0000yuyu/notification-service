package com.yeoljeong.tripmate.notification.infrastructure.external.push;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.NotificationSender;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.exception.FirebaseErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcmNotificationAdapter implements NotificationSender {

  private MulticastMessage createMessage(NotificationSendCommand command) {
    return MulticastMessage.builder()
        .addAllTokens(command.tokens())
        .setNotification(
            Notification
                .builder()
                .setTitle(command.title())
                .setBody(command.body())
                .build()
        ).build();
  }

  private BatchResponse sendPushMessageForMulticast(MulticastMessage message)
      throws FirebaseMessagingException {
    return FirebaseMessaging.getInstance().sendEachForMulticast(message);
  }

  @Override
  public NotificationSendResult send(NotificationSendCommand command) {
    MulticastMessage message = createMessage(command);
    if (command.tokens() != null) {
      try {
        BatchResponse response = sendPushMessageForMulticast(message);
        List<NotificationIndividualResult> details =
            response.getResponses().stream()
                .map(res ->
                    res.isSuccessful()
                        ? NotificationIndividualResult.success()
                        : NotificationIndividualResult.fail(res.getException().getMessage())
                )
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
    FirebaseErrorCode targetError = switch (fcmCode) {
      case UNREGISTERED -> FirebaseErrorCode.EXPIRED_TOKEN;
      case QUOTA_EXCEEDED -> FirebaseErrorCode.RATE_LIMIT_EXCEEDED;
      case INTERNAL, UNAVAILABLE -> FirebaseErrorCode.FCM_SERVER_ERROR;
      case INVALID_ARGUMENT -> FirebaseErrorCode.INVALID_NOTIFICATION_FORMAT;
      default -> FirebaseErrorCode.INTERNAL_NOTIFICATION_ERROR;
    };
    throw new BusinessException(targetError);
  }
}
