package ludogorie_soft.reservations_platform_api.service.impl;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.AllArgsConstructor;
import ludogorie_soft.reservations_platform_api.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
@Service
public class PaymentServiceImpl implements PaymentService {
    public PaymentServiceImpl() {
        Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY"); // Retrieve from environment
    }

    @Override
    public Map<String, Object> createPaymentIntent(String itemId) {

        int paymentAmount = calculatePrice(itemId);
        Map<String, Object> response = new HashMap<>();
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) paymentAmount)
                    .setCurrency("usd")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    .build();
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            response.put("clientSecret", paymentIntent.getClientSecret());
        } catch (Exception e) {
            response.put("error", "Payment failed: " + e.getMessage());
        }
        return response;
    }
    private int calculatePrice(String itemId) {
        Map<String, Integer> prices = new HashMap<>();
        prices.put("item1", 1000);
        prices.put("item2", 2000);
        return prices.getOrDefault(itemId, 1000);
    }
}
