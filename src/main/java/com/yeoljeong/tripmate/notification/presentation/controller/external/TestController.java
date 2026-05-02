package com.yeoljeong.tripmate.notification.presentation.controller.external;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendCommand;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationSendService;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

  private final NotificationSendService notificationSendService;

  public TestController(NotificationSendService notificationSendService) {
    this.notificationSendService = notificationSendService;
  }

  @GetMapping()
  public void getEvent() {
    NotificationSendCommand sendCommand = NotificationSendCommand.builder()
        .tokens(List.of("hakty6203@gmail.com", "hakty6203@yu.ac.kr"))
        .title("메일 테스트 발송").body("메일 발송").channelType(ChannelType.EMAIL).build();
    notificationSendService.send(sendCommand);
  }
}
