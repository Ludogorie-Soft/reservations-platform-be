package ludogorie_soft.reservations_platform_api.service.impl;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.service.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    public PaymentServiceImpl(Property property) {
        Stripe.apiKey = property.getSecretKey();
    }

    @Override
    public Map<String, Object> createPaymentIntent(String totalPrice) {
        Map<String, Object> response = new HashMap<>();
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(new BigDecimal(totalPrice).multiply(new BigDecimal(100)).longValue())
                    .setCurrency("usd")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            response.put("paymentIntentId", paymentIntent.getId());
            response.put("clientSecret", paymentIntent.getClientSecret());
        } catch (IllegalArgumentException e) {
            response.put("error", "Invalid input: " + e.getMessage());
        } catch (Exception e) {
            response.put("error", "Payment creation failed.");
        }
        return response;
    }
}

