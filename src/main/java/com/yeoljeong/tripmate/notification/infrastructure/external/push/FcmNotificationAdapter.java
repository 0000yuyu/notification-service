package com.yeoljeong.tripmate.notification.infrastructure.external.push;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendEachCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendMulticastCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendTarget;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.NotificationSender;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.exception.NotificationSendErrorCode;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcmNotificationAdapter implements NotificationSender {

  private MulticastMessage createMulticastMessage(NotificationSendMulticastCommand command) {
    return MulticastMessage.builder()
        .addAllTokens(command.targetTokens())
        .setNotification(
            Notification
                .builder()
                .setTitle(command.title())
                .setBody(command.body())
                .build()
        ).build();
  }

  private List<Message> createMessageList(List<NotificationSendTarget> targets) {
    return targets.stream().map(
        target ->
            Message.builder()
                .setNotification(
                    Notification.builder()
                        .setTitle(target.title())
                        .setBody(target.body())
                        .build())
                .setToken(target.token())
                .build()
    ).toList();
  }

  private BatchResponse sendPushMessageForMulticast(MulticastMessage message)
      throws FirebaseMessagingException {
    return FirebaseMessaging.getInstance().sendEachForMulticast(message);
  }

  private BatchResponse sendPushMessageForEach(List<Message> messages)
      throws FirebaseMessagingException {
    return FirebaseMessaging.getInstance().sendEach(messages);
  }

  private NotificationSendResult toResult(BatchResponse batchResponse) {
    List<SendResponse> responses = batchResponse.getResponses();

    List<NotificationIndividualResult> details = IntStream.range(0, responses.size())
        .mapToObj(index -> {
          SendResponse res = responses.get(index);
          return res.isSuccessful()
              ? NotificationIndividualResult.success(index)
              : NotificationIndividualResult.fail(index, res.getException().getMessage());
        })
        .toList();
    return NotificationSendResult.from(details);
  }

  @Override
  public NotificationSendResult sendMulticast(NotificationSendMulticastCommand command) {
    MulticastMessage message = createMulticastMessage(command);
    if (command.targetTokens() != null) {
      try {
        BatchResponse response = sendPushMessageForMulticast(message);
        return toResult(response);
      } catch (FirebaseMessagingException e) {
        handleFcmException(e);
      }
    }
    return null;
  }

  @Override
  public NotificationSendResult sendEach(NotificationSendEachCommand command) {
    List<Message> messages = createMessageList(command.targets());
    try {
      BatchResponse response = sendPushMessageForEach(messages);
      return toResult(response);
    } catch (FirebaseMessagingException e) {
      handleFcmException(e);
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
