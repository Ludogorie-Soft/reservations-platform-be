package ludogorie_soft.reservations_platform_api.service.impl;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.AllArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.service.BookingService;
import ludogorie_soft.reservations_platform_api.service.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BookingService bookingService;
    private final PropertyRepository propertyRepository;

    @Override
    public Map<String, Object> createPaymentIntent(UUID bookingId) {
        BookingResponseWithCustomerDataDto booking = bookingService.getBooking(bookingId);
        String totalPrice = String.valueOf(booking.getBookingResponseDto().getTotalPrice());

        Property property = propertyRepository.findById(booking.getBookingResponseDto().getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (property.getStripeSecretKey() == null || property.getStripeSecretKey().isEmpty()) {
            throw new IllegalArgumentException("Stripe secret key is missing for this property.");
        }

        Stripe.apiKey = property.getStripeSecretKey();

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

