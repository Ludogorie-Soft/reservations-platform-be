package ludogorie_soft.reservations_platform_api.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.el.PropertyNotFoundException;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.helper.BookingTestHelper;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Booking booking;
    private UUID bookingId;
    private BookingResponseWithCustomerDataDto mockBooking;

    @BeforeEach
    void setUp() {
        booking = BookingTestHelper.createBooking();
        bookingId = booking.getId();

        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setTotalPrice(booking.getTotalPrice());
        bookingResponseDto.setPropertyId(booking.getProperty().getId());

        mockBooking = new BookingResponseWithCustomerDataDto();
        mockBooking.setBookingResponseDto(bookingResponseDto);
    }

    @Test
    void testCreatePaymentIntent_Success() {
        // Arrange
        when(bookingService.getBooking(bookingId)).thenReturn(mockBooking);
        when(propertyRepository.findById(booking.getProperty().getId())).thenReturn(java.util.Optional.of(booking.getProperty()));

        try (MockedStatic<PaymentIntent> paymentIntentMockedStatic = Mockito.mockStatic(PaymentIntent.class)) {
            PaymentIntent mockPaymentIntent = Mockito.mock(PaymentIntent.class);
            when(mockPaymentIntent.getId()).thenReturn("pi_mock123");
            when(mockPaymentIntent.getClientSecret()).thenReturn("secret_mock123");

            paymentIntentMockedStatic.when(() ->
                    PaymentIntent.create(any(PaymentIntentCreateParams.class), any(RequestOptions.class))
            ).thenReturn(mockPaymentIntent);

            // Act
            Map<String, Object> response = paymentService.createPaymentIntent(bookingId);

            // Assert
            assertEquals("pi_mock123", response.get("paymentIntentId"));
            assertEquals("secret_mock123", response.get("clientSecret"));
        }
    }

    @Test
    void testCreatePaymentIntent_Failure_PaymentError() {
        when(bookingService.getBooking(bookingId)).thenReturn(mockBooking);
        when(propertyRepository.findById(booking.getProperty().getId())).thenReturn(java.util.Optional.of(booking.getProperty()));

        try (MockedStatic<PaymentIntent> paymentIntentMockedStatic = Mockito.mockStatic(PaymentIntent.class)) {
            StripeException mockException = Mockito.mock(StripeException.class);

            paymentIntentMockedStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenThrow(mockException);

            Map<String, Object> response = paymentService.createPaymentIntent(bookingId);

            assertTrue(response.containsKey("error"));
            assertEquals("Payment creation failed.", response.get("error"));
        }
    }

    @Test
    void testCreatePaymentIntent_Failure_MissingProperty() {
        when(bookingService.getBooking(bookingId)).thenReturn(mockBooking);
        when(propertyRepository.findById(booking.getProperty().getId())).thenReturn(java.util.Optional.empty());

        try {
            paymentService.createPaymentIntent(bookingId);
        } catch (PropertyNotFoundException e) {
            assertEquals("Property not found", e.getMessage());
        }
    }

    @Test
    void testCreatePaymentIntent_Failure_MissingSecretKey() {
        booking.getProperty().setStripeSecretKey(null);

        when(bookingService.getBooking(bookingId)).thenReturn(mockBooking);
        when(propertyRepository.findById(booking.getProperty().getId())).thenReturn(java.util.Optional.of(booking.getProperty()));

        try {
            paymentService.createPaymentIntent(bookingId);
        } catch (IllegalArgumentException e) {
            assertEquals("Stripe secret key is missing for this property.", e.getMessage());
        }
    }

}
