package com.yeoljeong.tripmate.notification.infrastructure.external.mail;

import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendEachCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendMulticastCommand;
import com.yeoljeong.tripmate.notification.application.dto.command.NotificationSendTarget;
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
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailNotificationAdapter implements NotificationSender {

  private final JavaMailSender javaMailSender;

  private MimeMessage createSingleMessage(String title, String body, String mail)
      throws MessagingException {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
    mimeMessageHelper.setSubject(title);
    mimeMessageHelper.setTo(mail);
    mimeMessageHelper.setText(body, true);
    return mimeMessage;
  }

  @Override
  public NotificationSendResult sendMulticast(NotificationSendMulticastCommand command) {
    List<NotificationIndividualResult> results = new ArrayList<>();
    for (int i = 0; i < command.targetTokens().size(); i++) {
      try {
        if (command.targetTokens().get(i) != null) {
          javaMailSender.send(
              createSingleMessage(command.title(), command.body(), command.targetTokens().get(i)));
        }
        results.add(NotificationIndividualResult.success(i));
      } catch (MailException | MessagingException e) {
        results.add(NotificationIndividualResult.fail(i, e.getMessage()));
      }
    }
    return NotificationSendResult.from(results);
  }

  @Override
  public NotificationSendResult sendEach(NotificationSendEachCommand command) {
    List<NotificationIndividualResult> results = new ArrayList<>();
    for (int i = 0; i < command.targets().size(); i++) {
      try {
        NotificationSendTarget target = command.targets().get(i);
        javaMailSender.send(
            createSingleMessage(target.title(), target.body(),
                target.token()));
        results.add(NotificationIndividualResult.success(i));
      } catch (MailException | MessagingException e) {
        results.add(NotificationIndividualResult.fail(i, e.getMessage()));
      }
    }
    return NotificationSendResult.from(results);
  }

  @Override
  public boolean supports(ChannelType type) {
    return type == ChannelType.EMAIL;
  }
}
