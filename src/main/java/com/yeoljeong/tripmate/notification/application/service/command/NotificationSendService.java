package com.yeoljeong.tripmate.notification.application.service.command;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.exception.constants.CommonErrorCode;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.NotificationSender;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationSendService {

  private final List<NotificationSender> senders;

  public NotificationSendResult send(NotificationSendCommand command) {
    NotificationSender sender = senders.stream()
        .filter(senders -> senders.supports(command.channelType()))
        .findFirst()
        .orElseThrow(() -> new BusinessException(CommonErrorCode.METHOD_NOT_ALLOWED));
    return sender.send(command);
  }
}
