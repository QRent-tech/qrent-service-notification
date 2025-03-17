package ee.qrental.email.core.service.messagestrategy;

import ee.qrental.email.api.in.request.EmailSendRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;

public interface LetterBuildStrategy {
    boolean canApply(final EmailSendRequest emailSendRequest);

  MimeMessage process(final EmailSendRequest emailSendRequest, final MimeMessage message)
      throws MessagingException, IOException;
}
