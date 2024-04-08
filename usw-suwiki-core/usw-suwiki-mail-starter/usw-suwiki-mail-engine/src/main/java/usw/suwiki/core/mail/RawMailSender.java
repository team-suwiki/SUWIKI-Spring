package usw.suwiki.core.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.MailException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
class RawMailSender {
  private static final String SUBJECT = "수원대학교 강의평가 플랫폼 SUWIKI 입니다.";
  private static final String FROM = "no-reply@suwiki.kr";

  private final TemplateEngine templateEngine;
  private final JavaMailSender mailSender;

  @Async
  public void send(Mail mail) {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(message, UTF_8.name());
    setContents(messageHelper, mail);
    mailSender.send(message);
  }

  private void setContents(MimeMessageHelper messageHelper, Mail mail) {
    String content = templateEngine.process(mail.template(), mail.context());

    try {
      messageHelper.setText(content, true);
      messageHelper.setSubject(SUBJECT);
      messageHelper.setTo(mail.to());
      messageHelper.setFrom(FROM);
    } catch (MessagingException e) {
      throw new MailException(ExceptionType.SEND_MAIL_FAILED);
    }
  }
}
