package ee.qrental.email.core.service;

import ee.qrental.email.api.in.request.EmailSendRequest;
import ee.qrental.email.core.service.messagestrategy.LetterBuildStrategy;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

import static ee.qrental.email.api.in.request.EmailType.INVOICE;

@AllArgsConstructor
public class InvoiceLetterBuildStrategy implements LetterBuildStrategy {

  private final TemplateEngine templateEngine;

  @Override
  public boolean canApply(EmailSendRequest emailSendRequest) {
    return INVOICE == emailSendRequest.getType();
  }

  @Override
  public MimeMessage process(final EmailSendRequest emailSendRequest, final MimeMessage message)
      throws MessagingException, IOException {
    final var invoiceNumber = emailSendRequest.getProperties().get("invoiceNumber").toString();
    final var recipients = emailSendRequest.getRecipients().toArray(new String[0]);
    final var from = "billing@qrent.ee";
    final var subject = "Invoice: " + invoiceNumber;
    final var attachmentFileName = String.format("Invoice-%s.pdf", invoiceNumber);
    final var attachment = new ByteArrayResource(emailSendRequest.getAttachment().readAllBytes());
    final var emailText = getEmailText(invoiceNumber);

    final var helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setSubject(subject);
    helper.setText(emailText, true);
    helper.setTo(recipients);
    helper.setFrom(from);
    helper.addAttachment(attachmentFileName, attachment);

    return message;
  }

  private String getEmailText(final String invoiceNumber) {
    final var invoiceTemplate = "invoiceMailTemplate";
    final var context = new Context();
    context.setVariable("invoiceNumber", invoiceNumber);

    return templateEngine.process(invoiceTemplate, context);
  }
}
