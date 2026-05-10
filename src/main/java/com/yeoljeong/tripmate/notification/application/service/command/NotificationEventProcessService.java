package com.yeoljeong.tripmate.notification.application.service.command;

import com.yeoljeong.tripmate.notification.application.dto.command.EventProcessCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationHistoryCreateCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationOutboxCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.TemplateMessageResult;
import com.yeoljeong.tripmate.notification.application.port.NotificationOutboxPort;
import com.yeoljeong.tripmate.notification.application.provider.NotificationContentProvider;
import com.yeoljeong.tripmate.notification.domain.constants.TokenActiveStatus;
import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import com.yeoljeong.tripmate.notification.domain.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationEventProcessService {

  private final NotificationContentProvider notificationContentProvider;
  private final NotificationCommandService notificationCommandService;
  private final NotificationOutboxPort notificationOutboxPort;
  private final NotificationRepository notificationRepository;

  public void process(EventProcessCommand processCommand) {

    TemplateMessageResult messageResult = notificationContentProvider.build(
        processCommand.topicName(), processCommand.channelType(), processCommand.payload()
    );

    List<NotificationHistory> histories = notificationCommandService.createHistories(
        NotificationHistoryCreateCommand
            .toHistoryCommand
                (
                    processCommand,
                    messageResult.title(),
                    messageResult.body()
                )
    );

    List<NotificationToken> tokenEntities = notificationRepository
        .findSendableTokens(
            processCommand.userList(),
            processCommand.channelType(),
            TokenActiveStatus.ACTIVE
        );

    List<NotificationOutboxCommand> sendOutboxes = tokenEntities.stream()
        .flatMap(token -> histories.stream()
            .filter(history -> history.getUserId().equals(token.getUserId()))
            .map(history -> NotificationOutboxCommand.of(
                processCommand.topicName(),
                history.getId(),
                processCommand.notificationType(),
                processCommand.channelType(),
                token.getId(),
                messageResult.toString())))
        .toList();
    notificationOutboxPort.publish(sendOutboxes);
  }
}
