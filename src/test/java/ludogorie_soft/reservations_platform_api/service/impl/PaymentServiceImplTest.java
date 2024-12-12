package ludogorie_soft.reservations_platform_api.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class PaymentServiceImplTest {

    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl();
    }

    @Test
    void testCreatePaymentIntent_Success() {
        try (MockedStatic<PaymentIntent> paymentIntentMockedStatic = Mockito.mockStatic(PaymentIntent.class)) {
            PaymentIntent mockPaymentIntent = Mockito.mock(PaymentIntent.class);
            Mockito.when(mockPaymentIntent.getId()).thenReturn("pi_mock123");
            Mockito.when(mockPaymentIntent.getClientSecret()).thenReturn("secret_mock123");

            paymentIntentMockedStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(mockPaymentIntent);

            Map<String, Object> response = paymentService.createPaymentIntent("100.00");

            assertEquals("pi_mock123", response.get("paymentIntentId"));
            assertEquals("secret_mock123", response.get("clientSecret"));
        }
    }

    @Test
    void testCreatePaymentIntent_Failure() {
        try (MockedStatic<PaymentIntent> paymentIntentMockedStatic = Mockito.mockStatic(PaymentIntent.class)) {
            StripeException mockException = Mockito.mock(StripeException.class);
            Mockito.when(mockException.getMessage()).thenReturn("Test Stripe exception");

            paymentIntentMockedStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenThrow(mockException);

            Map<String, Object> response = paymentService.createPaymentIntent("100.00");

            assertTrue(response.containsKey("error"));
            assertEquals("Payment creation failed.", response.get("error"));
        }
    }
}
