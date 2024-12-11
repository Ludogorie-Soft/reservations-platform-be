package ludogorie_soft.reservations_platform_api.service;

public interface MailService {

    void sendConfirmationEmail(String recipientEmail, String confirmationUrl);
}
