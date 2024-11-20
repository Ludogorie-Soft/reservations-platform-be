package ludogorie_soft.reservations_platform_api.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

        String fromEmail = "noreply@example.com";
        ReflectionTestUtils.setField(mailService, "emailAddress", fromEmail);
    }

    @Test
    void testSendConfirmationEmail_Success() {
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
        doAnswer(invocation -> {
            throw new jakarta.mail.MessagingException("Mock MessagingException");
        }).when(mailSender).send(any(MimeMessage.class));

        // WHEN & THEN
        MailSendException exception = assertThrows(MailSendException.class, () ->
                mailService.sendConfirmationEmail(recipientEmail, confirmationUrl)
        );

        assertEquals("Could not send confirmation email", exception.getMessage());
        assertInstanceOf(MessagingException.class, exception.getCause());

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
