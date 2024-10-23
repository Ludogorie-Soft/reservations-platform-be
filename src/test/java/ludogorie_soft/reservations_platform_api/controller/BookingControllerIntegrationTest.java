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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BookingControllerIntegrationTest {

    private static final String BASE_BOOKING_URL = "/api/bookings";
    private static final String BASE_PROPERTY_URL = "/api/properties/";
    private static final String REGISTER_URL = "/auth/register";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private RegisterDto registerDto;
    private BookingRequestDto bookingRequestDto;
    private PropertyRequestDto propertyRequestDto;


    @BeforeEach
    void setup() {
        registerDto = UserTestHelper.createRegisterDto();
        bookingRequestDto = BookingTestHelper.createBookingRequest();
        propertyRequestDto = PropertyTestHelper.createPropertyRequest();
    }

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll();
        propertyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateBookingSuccessfully() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());

        //WHEN
        ResponseEntity<BookingResponseDto> response = createBookingInDb();

        //THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(bookingRequestDto.getStartDate().toString(), response.getBody().getStartDate());
        assertEquals(bookingRequestDto.getDescription(), response.getBody().getDescription());
    }

    @Test
    void testCreateBookingShouldThrowWhenStartDateIsBeforeEndDate() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        LocalDate startLocalDate = LocalDate.now().plusDays(2);
        LocalDate endLocalDate = startLocalDate.plusDays(7);
        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        bookingRequestDto.setStartDate(endDate);
        bookingRequestDto.setEndDate(startDate);

        //WHEN
        ResponseEntity<BookingResponseDto> response = createBookingInDb();

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateBookingShouldThrowWhenTheDatesAreInThePast() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        LocalDate startLocalDate = LocalDate.now().minusDays(7);
        LocalDate endLocalDate = startLocalDate.minusDays(2);
        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        bookingRequestDto.setStartDate(endDate);
        bookingRequestDto.setEndDate(startDate);

        //WHEN
        ResponseEntity<BookingResponseDto> response = createBookingInDb();

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateBookingShouldThrowWhenTheDatesAreNotAvailable() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        createBookingInDb();

        //WHEN
        ResponseEntity<BookingResponseDto> response = createBookingInDb();

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private ResponseEntity<String> createUserInDb() {
        return testRestTemplate
                .postForEntity(REGISTER_URL, registerDto, String.class);
    }

    private ResponseEntity<PropertyResponseDto> createPropertyInDb() {
        return testRestTemplate
                .postForEntity(BASE_PROPERTY_URL, propertyRequestDto, PropertyResponseDto.class);
    }

    private ResponseEntity<BookingResponseDto> createBookingInDb() {
        return testRestTemplate
                .postForEntity(BASE_BOOKING_URL, bookingRequestDto, BookingResponseDto.class);
    }
}