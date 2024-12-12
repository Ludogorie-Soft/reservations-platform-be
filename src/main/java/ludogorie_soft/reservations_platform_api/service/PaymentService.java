package ludogorie_soft.reservations_platform_api.service;
import java.util.Map;
public interface PaymentService {
    Map<String, Object> createPaymentIntent(String itemId);
}
