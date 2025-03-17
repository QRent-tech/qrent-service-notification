package ee.qrental.email.core.service;

import static ee.qrental.email.api.in.request.EmailType.USER_REGISTRATION;

import ee.qrental.email.api.in.request.EmailSendRequest;
import ee.qrental.email.core.service.messagestrategy.LetterBuildStrategy;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@AllArgsConstructor
public class UserRegistrationLetterBuildStrategy implements LetterBuildStrategy {

  private final TemplateEngine templateEngine;

  @Override
  public boolean canApply(EmailSendRequest emailSendRequest) {
    return USER_REGISTRATION == emailSendRequest.getType();
  }

  @Override
  public MimeMessage process(final EmailSendRequest emailSendRequest, final MimeMessage message)
      throws MessagingException {
    final var password = emailSendRequest.getProperties().get("password").toString();
    final var username = emailSendRequest.getProperties().get("username").toString();
    final var firstName = emailSendRequest.getProperties().get("firstName").toString();
    final var lastName = emailSendRequest.getProperties().get("lastName").toString();
    final var userAccountInfo = String.format("%s %s", firstName, lastName);
    final var recipients = emailSendRequest.getRecipients().toArray(new String[0]);
    final var from = "admin@qrent.ee";
    final var subject = "Registration an User Account in Q-Rental billing system";

    final var emailText = getEmailText(userAccountInfo, username, password);

    final var helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setSubject(subject);
    helper.setText(emailText, true);
    helper.setTo(recipients);
    helper.setFrom(from);

    return message;
  }

  private String getEmailText(final String userAccountInfo,final String username, final String password) {
    final var templateFileName = "userAccountRegistrationMailTemplate";
    final var context = new Context();
    context.setVariable("userAccountInfo", userAccountInfo);
    context.setVariable("username", username);
    context.setVariable("password", password);

    return templateEngine.process(templateFileName, context);
  }
}
