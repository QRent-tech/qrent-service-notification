package ee.qrental.email.core.service;

import ee.qrental.email.api.in.request.EmailSendRequest;
import ee.qrental.email.api.in.usecase.EmailSendUseCase;
import ee.qrental.email.core.service.messagestrategy.LetterBuildStrategy;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;

@AllArgsConstructor
public class EmailSendService implements EmailSendUseCase {

  private final JavaMailSender mailSender;

  private List<LetterBuildStrategy> letterBuildStrategies;

  @Override
  public void sendEmail(final EmailSendRequest request) {
    try {
      final var message =
          letterBuildStrategies.stream()
              .filter(letterBuildStrategy -> letterBuildStrategy.canApply(request))
              .findFirst()
              .orElseThrow(
                  () ->
                      new RuntimeException(
                          "No Email Letter build Strategy were found for " + request.getType()))
              .process(request, mailSender.createMimeMessage());
      mailSender.send(message);
    } catch (final Exception e) {
      System.out.println("Email sending failed! Check the reason below: ");
      System.out.println(e.getMessage());
    }
  }
}
