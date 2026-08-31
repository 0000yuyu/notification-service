package com.yeoljeong.tripmate.notification.application.service.command;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.exception.constants.CommonErrorCode;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendEachCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendMulticastCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.NotificationSender;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationSendService {

  private final List<NotificationSender> senders;

  @PostConstruct
  public void checkSenders() {
    senders.forEach(sender ->
        log.warn(
            "[SENDER-BEAN] class={}",
            sender.getClass().getName()
        )
    );
  }

  public NotificationSendResult sendMulticast(NotificationSendMulticastCommand command) {
    NotificationSender sender = senders.stream()
        .filter(senders -> senders.supports(command.channelType()))
        .findFirst()
        .orElseThrow(() -> new BusinessException(CommonErrorCode.METHOD_NOT_ALLOWED));
    return sender.sendMulticast(command);
  }

  public NotificationSendResult sendEach(NotificationSendEachCommand command) {
    NotificationSender sender = senders.stream()
        .filter(senders -> senders.supports(command.channelType()))
        .findFirst()
        .orElseThrow(() -> new BusinessException(CommonErrorCode.METHOD_NOT_ALLOWED));
    return sender.sendEach(command);
  }
}
