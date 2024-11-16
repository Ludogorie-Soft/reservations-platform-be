package ludogorie_soft.reservations_platform_api.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "spring.mail.username=no-reply@yourdomain.com")
public class MailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailServiceImpl mailService;

    private String recipientEmail;
    private String confirmationUrl;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        recipientEmail = "test@example.com";
        confirmationUrl = "http://example.com/confirm";

        mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendConfirmationEmail_Success() throws MessagingException {
        // GIVEN - handled in @BeforeEach
        // WHEN
        mailService.sendConfirmationEmail(recipientEmail, confirmationUrl);

        // THEN
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendConfirmationEmail_MessagingException() {
        // GIVEN
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Could not send confirmation email"));

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> mailService.sendConfirmationEmail(recipientEmail, confirmationUrl));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
