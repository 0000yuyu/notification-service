package com.yeoljeong.tripmate.notification.application.service.command;

import com.yeoljeong.tripmate.notification.application.dto.command.EventProcessCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationHistoryCreateCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.dto.result.TemplateMessageResult;
import com.yeoljeong.tripmate.notification.application.provider.NotificationContentProvider;
import com.yeoljeong.tripmate.notification.domain.constants.TokenActiveStatus;
import com.yeoljeong.tripmate.notification.domain.model.NotificationHistory;
import com.yeoljeong.tripmate.notification.domain.model.NotificationToken;
import com.yeoljeong.tripmate.notification.infrastructure.dto.NotificationSendMessage;
import com.yeoljeong.tripmate.notification.infrastructure.persistence.jpa.NotificationTokenJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationEventProcessService {

  private final NotificationTokenJpaRepository notificationTokenJpaRepository;
  private final NotificationContentProvider notificationContentProvider;
  private final NotificationSendService notificationSendService;
  private final NotificationCommandService notificationCommandService;

  public void process(EventProcessCommand processCommand) {

    // 템플릿 조립
    TemplateMessageResult messageResult = notificationContentProvider.build(
        processCommand.topicName(), processCommand.channelType(), processCommand.payload()
    );

    // 히스토리 저장
    List<NotificationHistory> histories = notificationCommandService.createHistories(
        NotificationHistoryCreateCommand
            .toHistoryCommand
                (
                    processCommand,
                    messageResult.title(),
                    messageResult.body()
                )
    );

    // 토큰 가져오기
    List<NotificationToken> tokenEntities = notificationTokenJpaRepository
        .findSendableTokens(
            processCommand.userList(),
            processCommand.channelType(),
            TokenActiveStatus.ACTIVE
        );

    // 전송
    NotificationSendResult resultList = notificationSendService.send(
        NotificationSendMessage.builder()
            .title(messageResult.title())
            .body(messageResult.body())
            .targetTokens
                (
                    tokenEntities
                        .stream()
                        .map(NotificationToken::getTokenValue)
                        .toList()
                )
            .channelType(processCommand.channelType())
            .build()
    );
  }
}
