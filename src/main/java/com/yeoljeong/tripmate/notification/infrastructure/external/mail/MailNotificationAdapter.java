package com.yeoljeong.tripmate.notification.infrastructure.external.mail;

import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import com.yeoljeong.tripmate.notification.application.provider.NotificationSender;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.infrastructure.dto.NotificationSendMessage;
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

  private MimeMessage createMessage(NotificationSendMessage message, String mail)
      throws MessagingException {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
    mimeMessageHelper.setSubject(message.title());
    mimeMessageHelper.setTo(mail);
    mimeMessageHelper.setText(message.body(), true);
    return mimeMessage;
  }

  @Override
  public NotificationSendResult send(NotificationSendMessage message) {
    List<NotificationIndividualResult> results = new ArrayList<>();
    for (int i = 0; i < message.targetTokens().toArray().length; i++) {
      try {
        javaMailSender.send(createMessage(message, message.targetTokens().get(i)));
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
