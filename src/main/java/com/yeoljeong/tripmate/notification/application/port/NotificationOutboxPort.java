package com.yeoljeong.tripmate.notification.application.port;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationOutboxCommand;
import java.util.List;

public interface NotificationOutboxPort {

  void publish(List<NotificationOutboxCommand> commands);
}
