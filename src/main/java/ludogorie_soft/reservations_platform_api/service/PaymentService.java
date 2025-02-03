package ludogorie_soft.reservations_platform_api.service;
import java.util.Map;
import java.util.UUID;

public interface PaymentService {
    Map<String, Object> createPaymentIntent(UUID bookingId);
}
