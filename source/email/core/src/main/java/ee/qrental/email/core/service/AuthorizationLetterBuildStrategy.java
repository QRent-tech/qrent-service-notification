package ee.qrental.email.core.service;

import static ee.qrental.email.api.in.request.EmailType.AUTHORIZATION;

import ee.qrental.email.api.in.request.EmailSendRequest;
import ee.qrental.email.core.service.messagestrategy.LetterBuildStrategy;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@AllArgsConstructor
public class AuthorizationLetterBuildStrategy implements LetterBuildStrategy {

  private final TemplateEngine templateEngine;

  @Override
  public boolean canApply(EmailSendRequest emailSendRequest) {
    return AUTHORIZATION == emailSendRequest.getType();
  }

  @Override
  public MimeMessage process(final EmailSendRequest emailSendRequest, final MimeMessage message)
      throws MessagingException, IOException {
    final var isikukood = emailSendRequest.getProperties().get("isikukood").toString();
    final var recipients = emailSendRequest.getRecipients().toArray(new String[0]);
    final var from = "billing@qrent.ee";
    final var subject = "Authorization for Bolt";
    final var attachmentFileName = String.format("Authorization-%s.pdf", isikukood);
    final var attachment = new ByteArrayResource(emailSendRequest.getAttachment().readAllBytes());
    final var emailText = getEmailText();

    final var helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setSubject(subject);
    helper.setText(emailText, true);
    helper.setTo(recipients);
    helper.setFrom(from);
    helper.addAttachment(attachmentFileName, attachment);

    return message;
  }

  private String getEmailText() {
    final var template = "authorizationMailTemplate";
    final var context = new Context();

    return templateEngine.process(template, context);
  }
}
