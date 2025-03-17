package ee.qrental.email.core.service;

import static ee.qrental.email.api.in.request.EmailType.BONUS_CALCULATION;

import ee.qrental.email.api.in.request.EmailSendRequest;
import ee.qrental.email.core.service.messagestrategy.LetterBuildStrategy;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@AllArgsConstructor
public class BonusCalculationLetterBuildStrategy implements LetterBuildStrategy {

  private final TemplateEngine templateEngine;

  @Override
  public boolean canApply(EmailSendRequest emailSendRequest) {
    return BONUS_CALCULATION == emailSendRequest.getType();
  }

  @Override
  public MimeMessage process(final EmailSendRequest emailSendRequest, final MimeMessage message)
      throws MessagingException {
    final var calculationType = emailSendRequest.getProperties().get("calculationType").toString();
    final var recipients = emailSendRequest.getRecipients().toArray(new String[0]);
    final var from = "operation@qrent.ee";
    final var subject = "Calculation: " + calculationType;
    final var emailText = getEmailText(emailSendRequest.getProperties());

    final var helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setSubject(subject);
    helper.setText(emailText, true);
    helper.setTo(recipients);
    helper.setFrom(from);

    return message;
  }

  private String getEmailText(Map<String, Object> properties) {
    final var invoiceTemplate = "rentCalculationMailTemplate";
    final var context = new Context();
    context.setVariable("calculationType", properties.get("calculationType"));
    context.setVariable("calculationDate", properties.get("calculationDate"));
    context.setVariable("wekNumber", properties.get("wekNumber"));
    context.setVariable("transactions", properties.get("transactions"));

    return templateEngine.process(invoiceTemplate, context);
  }
}
