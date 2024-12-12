package ludogorie_soft.reservations_platform_api.controller;

import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.helper.BookingTestHelper;
import ludogorie_soft.reservations_platform_api.helper.PropertyTestHelper;
import ludogorie_soft.reservations_platform_api.helper.UserTestHelper;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.repository.UserRepository;
import ludogorie_soft.reservations_platform_api.service.BookingService;
import ludogorie_soft.reservations_platform_api.service.PaymentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PaymentControllerIntegrationTest {
    private static final String BASE_BOOKING_URL = "/api/bookings";
    private static final String BASE_PROPERTY_URL = "/api/properties";
    private static final String REGISTER_URL = "/auth/register";
    private static final String BASE_PAYMENT_URL = "/payment/create-payment-intent/";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @MockBean
    private PaymentService paymentService;

    private RegisterDto registerDto;
    private BookingRequestDto bookingRequestDto;
    private PropertyRequestDto propertyRequestDto;
    private HttpHeaders headers;

    @BeforeEach
    void setup() {
        registerDto = UserTestHelper.createRegisterDto();
        bookingRequestDto = BookingTestHelper.createBookingRequest();
        propertyRequestDto = PropertyTestHelper.createDefaultPropertyRequestDto();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
    }

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll();
        propertyRepository.deleteAll();
        userRepository.deleteAll();
        headers = new HttpHeaders();
    }

    @Test
    void createPaymentIntent_ShouldReturnValidResponse_WhenBookingExists() {
        //GIVEN
        ResponseEntity<BookingResponseDto> responseBooking = createBookingInDb();
        UUID bookingId = Objects.requireNonNull(responseBooking.getBody()).getId();
        HttpEntity<String> request = new HttpEntity<>(headers);

        //WHEN
        ResponseEntity<Map> response = testRestTemplate.postForEntity(
                BASE_PAYMENT_URL + bookingId, request, Map.class);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        System.out.println("body " + response.getBody());
    }

    @Test
    void testCreatePaymentShouldThrowWhenStripeThirdPartyNotWork() {
        //GIVEN
        ResponseEntity<BookingResponseDto> responseBooking = createBookingInDb();
        UUID bookingId = Objects.requireNonNull(responseBooking.getBody()).getId();
        HttpEntity<String> request = new HttpEntity<>(headers);

        Mockito.when(paymentService.createPaymentIntent(Mockito.any()))
                .thenThrow(new RuntimeException("Simulated Stripe API failure"));

        //WHEN
        ResponseEntity<Map> response = testRestTemplate.postForEntity(
                BASE_PAYMENT_URL + bookingId, request, Map.class);

        //THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    @Test
    void createPaymentIntent_ShouldReturnNotFound_WhenInvalidBookingIdProvided() {
        // GIVEN
        UUID invalidBookingId = UUID.randomUUID();
        HttpEntity<String> request = new HttpEntity<>(headers);

        // WHEN
        ResponseEntity<Map> response = testRestTemplate.postForEntity(
                BASE_PAYMENT_URL + invalidBookingId, request, Map.class);

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Booking with id + " + invalidBookingId + " not found!", response.getBody().get("message"));
    }

    private ResponseEntity<BookingResponseDto> createBookingInDb() {
        return testRestTemplate
                .postForEntity(BASE_BOOKING_URL, bookingRequestDto, BookingResponseDto.class);
    }

    private ResponseEntity<String> createUserInDb() {
        return testRestTemplate
                .postForEntity(REGISTER_URL, registerDto, String.class);
    }

    private ResponseEntity<PropertyResponseDto> createPropertyInDb() {
        return testRestTemplate
                .postForEntity(BASE_PROPERTY_URL, propertyRequestDto, PropertyResponseDto.class);
    }
}