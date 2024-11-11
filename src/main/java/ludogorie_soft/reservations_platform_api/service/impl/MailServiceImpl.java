package ludogorie_soft.reservations_platform_api.service.impl;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import ludogorie_soft.reservations_platform_api.service.MailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public void sendConfirmationEmail(String recipientEmail, String confirmationUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(recipientEmail);
            helper.setSubject("Confirm Your Reservation");
            helper.setText("<html><body><p>Thank you for your reservation!</p>" +
                    "<p>Please confirm it by clicking the link below:</p>" +
                    "<p><a href='" + confirmationUrl + "'>Confirm Reservation</a></p></body></html>", true);
            helper.setFrom("no-reply@yourdomain.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Could not send confirmation email", e);
        }
    }

}
