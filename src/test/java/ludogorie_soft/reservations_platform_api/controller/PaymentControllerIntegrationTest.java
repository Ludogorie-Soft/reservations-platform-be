package ludogorie_soft.reservations_platform_api.controller;

import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.exception.BookingNotFoundException;
import ludogorie_soft.reservations_platform_api.helper.BookingTestHelper;
import ludogorie_soft.reservations_platform_api.helper.PropertyTestHelper;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.service.BookingService;
import ludogorie_soft.reservations_platform_api.service.PaymentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PaymentControllerIntegrationTest {
    @MockBean
    private BookingService bookingService;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private PaymentController paymentController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookingRepository bookingRepository;

    private Booking booking;
    @BeforeEach
    void setup() {
        booking = BookingTestHelper.createBooking();
    }

    @AfterEach
    void cleanUp() {
        // Clean up the repository after each test
        bookingRepository.deleteAll();
    }

    @Test
    void createPaymentIntent_ShouldReturnValidResponse_WhenBookingExists() {

        UUID bookingId = booking.getId();
        String url = "/payment/create-payment-intent/" + bookingId;
        System.out.println("id " + bookingId);
        System.out.println("url " + url);

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("paymentIntentId", "pi_test123");
        mockResponse.put("clientSecret", "secret_test123");
        when(paymentService.createPaymentIntent(Mockito.any(String.class)))
                .thenReturn(mockResponse);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
       // ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("pi_test123", response.getBody().get("paymentIntentId"));
        assertEquals("secret_test123", response.getBody().get("clientSecret"));
    }


    @Test
    void createPaymentIntent_ShouldHandleInvalidBookingId() {
        // GIVEN

        UUID invalidBookingId = UUID.randomUUID();

        when(bookingService.getBooking(eq(invalidBookingId)))
                .thenThrow(new BookingNotFoundException("Booking with id " + invalidBookingId + " not found!"));

        // WHEN & THEN
        assertThrows(BookingNotFoundException.class, () -> {
            paymentController.createPaymentIntent(invalidBookingId);
        });

        verify(bookingService, times(1)).getBooking(invalidBookingId);
        verifyNoInteractions(paymentService);
    }

    @Test
    void createPaymentIntent_ShouldHandlePaymentServiceFailure() {
        // GIVEN
        UUID bookingId = UUID.randomUUID();
        BookingResponseDto bookingResponse = new BookingResponseDto();
        bookingResponse.setTotalPrice(BigDecimal.valueOf(150.00));

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("error", "Payment creation failed.");

        when(bookingService.getBooking(eq(bookingId))).thenReturn(bookingResponse);
        when(paymentService.createPaymentIntent(eq("150.00"))).thenReturn(expectedResponse);

        // WHEN
        Map<String, Object> response = paymentController.createPaymentIntent(bookingId);

        // THEN
        assertEquals("Payment creation failed.", response.get("error"));
        verify(bookingService, times(1)).getBooking(bookingId);
        verify(paymentService, times(1)).createPaymentIntent("150.00");
    }
}
