package ludogorie_soft.reservations_platform_api.config;

import java.util.Properties;

import com.icegreen.greenmail.util.GreenMail;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@TestConfiguration
public class MailSenderTestConfig {

    @Bean
    public GreenMail greenMail() {
        GreenMail greenMail = new GreenMail();
        greenMail.start();
        return greenMail;
    }

    @Bean
    public JavaMailSender javaMailSender(GreenMail greenMail) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(greenMail.getSmtp().getBindTo());
        mailSender.setPort(greenMail.getSmtp().getPort());
        mailSender.setProtocol("smtp");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", false);
        props.put("mail.smtp.starttls.enable", false);

        return mailSender;
    }
}