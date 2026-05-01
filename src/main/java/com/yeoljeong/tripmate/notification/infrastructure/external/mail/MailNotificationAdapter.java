package com.yeoljeong.tripmate.notification.infrastructure.external.mail;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendCommand;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.NotificationSender;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailNotificationAdapter implements NotificationSender {

  private final JavaMailSender javaMailSender;

  private MimeMessage createMessage(NotificationSendCommand command, String mail)
      throws MessagingException {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
    mimeMessageHelper.setSubject(command.body());
    mimeMessageHelper.setTo(mail);
    mimeMessageHelper.setText(command.body());
    return mimeMessage;
  }

  @Override
  public NotificationSendResult send(NotificationSendCommand command) {
    List<NotificationIndividualResult> results = new ArrayList<>();
    for (int i = 0; i < command.tokens().toArray().length; i++) {
      try {
        javaMailSender.send(createMessage(command, command.tokens().get(i)));
        results.add(NotificationIndividualResult.success());
      } catch (MessagingException e) {
        results.add(NotificationIndividualResult.fail(e.getMessage()));
      }
    }
    return null;
  }

  @Override
  public boolean supports(ChannelType type) {
    return type == ChannelType.EMAIL;
  }
}
