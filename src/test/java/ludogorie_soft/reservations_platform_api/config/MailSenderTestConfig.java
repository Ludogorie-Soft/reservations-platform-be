package ludogorie_soft.reservations_platform_api.config;

import java.util.Properties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@TestConfiguration
public class MailSenderTestConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(3025);
        mailSender.setProtocol("smtp");
        mailSender.setUsername("");
        mailSender.setPassword("");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", false);
        props.put("mail.smtp.starttls.enable", false);

        return mailSender;
    }
}