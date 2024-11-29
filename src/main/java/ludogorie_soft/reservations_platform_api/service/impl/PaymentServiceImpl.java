package ludogorie_soft.reservations_platform_api.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import ludogorie_soft.reservations_platform_api.service.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    public PaymentServiceImpl() {
        Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY");
    }

    @Override
    public Map<String, Object> createPaymentIntent(String totalPrice) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (totalPrice == null || totalPrice.isEmpty()) {
                throw new IllegalArgumentException("Total price cannot be null or empty");
            }

            System.out.println("Starting payment intent creation");

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(new BigDecimal(totalPrice).multiply(new BigDecimal(100)).longValue())
                    .setCurrency("usd")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            System.out.println("Created PaymentIntent with ID: " + paymentIntent.getId());

            response.put("paymentIntentId", paymentIntent.getId());
            response.put("clientSecret", paymentIntent.getClientSecret());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input: " + e.getMessage());
            response.put("error", "Invalid input: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error creating payment intent: " + e.getMessage());
            response.put("error", "Payment creation failed.");
        }
        return response;
    }
}

    //only for testing through POSTMAN
//    @Override
//    public Map<String, Object> confirmPayment(String paymentIntentId) {
//        Map<String, Object> response = new HashMap<>();
//        String successLink = "https://your-website.com/payment/success";
//        String cancelLink = "https://your-website.com/payment/cancel";
//
//        try {
//            PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
//                    //"pm_card_visa" to test successful payment
//                    //"pm_card_chargeDeclined" to test declined payment
//                    .setPaymentMethod("pm_card_visa")
//                    .setReturnUrl(successLink)
//                    .build();
//
//            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
//            paymentIntent = paymentIntent.confirm(params);
//
//            if ("requires_payment_method".equals(paymentIntent.getStatus())) {
//                response.put("error", "Payment was canceled or requires a new payment method.");
//                response.put("redirect", cancelLink);
//            } else {
//                response.put("status", paymentIntent.getStatus());
//            }
//
//        } catch (StripeException e) {
//            response.put("error", "Stripe error: " + e.getMessage());
//        }
//        return response;
//    }

//    private int calculatePrice(String itemId) {
//        Map<String, Integer> prices = new HashMap<>();
//        prices.put("item1", 1000);
//        prices.put("item2", 2000);
//        return prices.getOrDefault(itemId, 1000);
//    }

