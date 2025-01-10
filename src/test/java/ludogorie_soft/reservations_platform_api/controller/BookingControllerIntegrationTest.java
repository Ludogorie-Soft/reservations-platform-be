package ludogorie_soft.reservations_platform_api.controller;

import com.icegreen.greenmail.util.GreenMail;
import jakarta.mail.internet.MimeMessage;
import ludogorie_soft.reservations_platform_api.ReservationsPlatformApiApplication;
import ludogorie_soft.reservations_platform_api.config.MailSenderTestConfig;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestCustomerDataDto;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Customer;
import ludogorie_soft.reservations_platform_api.helper.BookingTestHelper;
import ludogorie_soft.reservations_platform_api.helper.CustomerTestHelper;
import ludogorie_soft.reservations_platform_api.helper.PropertyTestHelper;
import ludogorie_soft.reservations_platform_api.helper.UserTestHelper;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.ConfirmationTokenRepository;
import ludogorie_soft.reservations_platform_api.repository.CustomerRepository;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {
    ReservationsPlatformApiApplication.class})
@ActiveProfiles("test")
@Import(MailSenderTestConfig.class)
class BookingControllerIntegrationTest {

    private static final String BASE_BOOKING_URL = "/api/bookings";
    private static final String BASE_PROPERTY_URL = "/api/properties";
    private static final String REGISTER_URL = "/auth/register";
    private static final String CUSTOMER_DATA_URL = BASE_BOOKING_URL + "/customer-data";

    @Autowired
    private GreenMail greenMail;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    private RegisterDto registerDto;
    private BookingRequestDto bookingRequestDto;
    private PropertyRequestDto propertyRequestDto;
    private HttpHeaders headers;
    private Customer customer;
    private BookingRequestCustomerDataDto validBookingRequestCustomerDataDto;
    private BookingRequestCustomerDataDto invalidBookingRequestCustomerDataDto;

    @BeforeEach
    void setup() {
        registerDto = UserTestHelper.createRegisterDto();
        bookingRequestDto = BookingTestHelper.createBookingRequest();
        propertyRequestDto = PropertyTestHelper.createDefaultPropertyRequestDto();
        customer = CustomerTestHelper.createCustomer();

        validBookingRequestCustomerDataDto = BookingTestHelper.createBookingRequestWithCustomerDataWithoutBooking(customer);
        invalidBookingRequestCustomerDataDto = BookingTestHelper.createBookingRequestWithInvalidBookingId();
    }

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll();
        propertyRepository.deleteAll();
        userRepository.deleteAll();
        confirmationTokenRepository.deleteAll();
        customerRepository.deleteAll();
        headers = new HttpHeaders();
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
        assertEquals(calculateBookingPrice(bookingRequestDto, propertyResponse.getBody()), response.getBody().getTotalPrice());
    }

    @Test
    void testCreateBookingSuccessfullyWithoutPetContent() {
        //GIVEN
        createUserInDb();
        propertyRequestDto.setPetAllowed(true);
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        bookingRequestDto.setPetContent(false);

        //WHEN
        ResponseEntity<BookingResponseDto> response = createBookingInDb();

        //THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(bookingRequestDto.getStartDate().toString(), response.getBody().getStartDate());
        assertEquals(bookingRequestDto.getDescription(), response.getBody().getDescription());
        assertEquals(calculateBookingPrice(bookingRequestDto, propertyResponse.getBody()), response.getBody().getTotalPrice());
        assertEquals(bookingRequestDto.isPetContent(), response.getBody().isPetContent());
    }

    @Test
    void testCreateBookingSuccessfullyWithPetContent() {
        //GIVEN
        createUserInDb();
        propertyRequestDto.setPetAllowed(true);
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        bookingRequestDto.setPetContent(true);

        //WHEN
        ResponseEntity<BookingResponseDto> response = createBookingInDb();

        //THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(bookingRequestDto.getStartDate().toString(), response.getBody().getStartDate());
        assertEquals(bookingRequestDto.getDescription(), response.getBody().getDescription());
        assertEquals(calculateBookingPrice(bookingRequestDto, propertyResponse.getBody()), response.getBody().getTotalPrice());
        assertEquals(bookingRequestDto.isPetContent(), response.getBody().isPetContent());
    }

    @Test
    void testCreateBooking_ShouldThrow_WhenPetExistsButIsNotAllowed() {
        //GIVEN
        createUserInDb();
        propertyRequestDto.setPetAllowed(false);
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        bookingRequestDto.setPetContent(true);

        //WHEN
        ResponseEntity<BookingResponseDto> response = createBookingInDb();

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateBookingShouldThrowWhenStartDateIsAfterEndDate() {
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

        // WHEN: Send request
        ResponseEntity<List> response = testRestTemplate.postForEntity(BASE_BOOKING_URL, bookingRequestDto, List.class);

        // THEN: Verify response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        List<String> errors = response.getBody();

        // Check specific validation messages
        assertTrue(errors.contains("startDate: Start date cannot be in the past"));
        assertTrue(errors.contains("endDate: End date cannot be in the past"));
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

    @Test
    void testCreateBookingShouldThrowWhenPeopleAreMoreThanCapacity() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        bookingRequestDto.setAdultCount(20);

        //WHEN
        ResponseEntity<BookingResponseDto> response = createBookingInDb();

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testEditBookingSuccessfully() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        ResponseEntity<BookingResponseDto> createBookingResponse = createBookingInDb();

        assertEquals(HttpStatus.CREATED, createBookingResponse.getStatusCode());
        assertEquals(bookingRequestDto.getDescription(), Objects.requireNonNull(createBookingResponse.getBody()).getDescription());
        assertEquals(bookingRequestDto.getAdultCount(), createBookingResponse.getBody().getAdultCount());

        BookingRequestDto editBookingRequest = BookingTestHelper.createBookingRequest();
        editBookingRequest.setDescription("New description");
        editBookingRequest.setAdultCount(1);


        HttpEntity<BookingRequestDto> requestEntity = new HttpEntity<>(editBookingRequest, headers);

        //WHEN
        ResponseEntity<BookingResponseDto> response = testRestTemplate.exchange(
                BASE_BOOKING_URL + "/" + createBookingResponse.getBody().getId()
                , HttpMethod.PUT,
                requestEntity,
                BookingResponseDto.class);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(editBookingRequest.getDescription(), response.getBody().getDescription());
        assertEquals(editBookingRequest.getAdultCount(), response.getBody().getAdultCount());
    }

    @Test
    void testEditBookingShouldThrowWhenNewDatesAreInThePast() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        ResponseEntity<BookingResponseDto> createBookingResponse = createBookingInDb();

        assertEquals(HttpStatus.CREATED, createBookingResponse.getStatusCode());
        assertEquals(bookingRequestDto.getDescription(), Objects.requireNonNull(createBookingResponse.getBody()).getDescription());
        assertEquals(bookingRequestDto.getAdultCount(), createBookingResponse.getBody().getAdultCount());

        BookingRequestDto editBookingRequest = BookingTestHelper.createBookingRequest();
        LocalDate startLocalDate = LocalDate.now().minusDays(7);
        LocalDate endLocalDate = startLocalDate.minusDays(2);
        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        editBookingRequest.setStartDate(startDate);
        editBookingRequest.setEndDate(endDate);

        HttpEntity<BookingRequestDto> requestEntity = new HttpEntity<>(editBookingRequest, headers);

        //WHEN
        ResponseEntity<BookingResponseDto> response = testRestTemplate.exchange(
                BASE_BOOKING_URL + "/" + createBookingResponse.getBody().getId()
                , HttpMethod.PUT,
                requestEntity,
                BookingResponseDto.class);

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testEditBookingShouldThrowWhenEndDateIsBeforeStartDate() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        ResponseEntity<BookingResponseDto> createBookingResponse = createBookingInDb();

        assertEquals(HttpStatus.CREATED, createBookingResponse.getStatusCode());
        assertEquals(bookingRequestDto.getDescription(), Objects.requireNonNull(createBookingResponse.getBody()).getDescription());
        assertEquals(bookingRequestDto.getAdultCount(), createBookingResponse.getBody().getAdultCount());

        BookingRequestDto editBookingRequest = BookingTestHelper.createBookingRequest();
        LocalDate startLocalDate = LocalDate.now().plusDays(2);
        LocalDate endLocalDate = startLocalDate.plusDays(7);
        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        editBookingRequest.setStartDate(endDate);
        editBookingRequest.setEndDate(startDate);

        HttpEntity<BookingRequestDto> requestEntity = new HttpEntity<>(editBookingRequest, headers);

        //WHEN
        ResponseEntity<BookingResponseDto> response = testRestTemplate.exchange(
                BASE_BOOKING_URL + "/" + createBookingResponse.getBody().getId()
                , HttpMethod.PUT,
                requestEntity,
                BookingResponseDto.class);

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testEditBookingShouldThrowWhenTheDatesAreNotAvailable() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        ResponseEntity<BookingResponseDto> createBookingResponse = createBookingInDb();

        assertEquals(HttpStatus.CREATED, createBookingResponse.getStatusCode());
        assertEquals(bookingRequestDto.getDescription(), Objects.requireNonNull(createBookingResponse.getBody()).getDescription());
        assertEquals(bookingRequestDto.getAdultCount(), createBookingResponse.getBody().getAdultCount());

        BookingRequestDto secondBookingRequest = BookingTestHelper.createBookingRequest();
        LocalDate startLocalDate = LocalDate.now().plusDays(10);
        LocalDate endLocalDate = startLocalDate.plusDays(12);
        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        secondBookingRequest.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        secondBookingRequest.setStartDate(startDate);
        secondBookingRequest.setEndDate(endDate);

        ResponseEntity<BookingResponseDto> createSecondBooking = testRestTemplate.postForEntity(
                BASE_BOOKING_URL, secondBookingRequest, BookingResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, createSecondBooking.getStatusCode());

        BookingRequestDto editBookingRequest = BookingTestHelper.createBookingRequest();
        editBookingRequest.setStartDate(secondBookingRequest.getStartDate());
        editBookingRequest.setEndDate(secondBookingRequest.getEndDate());

        HttpEntity<BookingRequestDto> requestEntity = new HttpEntity<>(editBookingRequest, headers);

        //WHEN
        ResponseEntity<BookingResponseDto> response = testRestTemplate.exchange(
                BASE_BOOKING_URL + "/" + createBookingResponse.getBody().getId()
                , HttpMethod.PUT,
                requestEntity,
                BookingResponseDto.class);

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testEditBookingShouldThrowWhenPeopleAreMoreThanCapacity() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        ResponseEntity<BookingResponseDto> createBookingResponse = createBookingInDb();

        assertEquals(HttpStatus.CREATED, createBookingResponse.getStatusCode());
        assertEquals(bookingRequestDto.getDescription(), Objects.requireNonNull(createBookingResponse.getBody()).getDescription());
        assertEquals(bookingRequestDto.getAdultCount(), createBookingResponse.getBody().getAdultCount());

        BookingRequestDto editBookingRequest = BookingTestHelper.createBookingRequest();
        editBookingRequest.setAdultCount(20);

        HttpEntity<BookingRequestDto> requestEntity = new HttpEntity<>(editBookingRequest, headers);

        //WHEN
        ResponseEntity<BookingResponseDto> response = testRestTemplate.exchange(
                BASE_BOOKING_URL + "/" + createBookingResponse.getBody().getId()
                , HttpMethod.PUT,
                requestEntity,
                BookingResponseDto.class);

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetBookingSuccessfully() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        ResponseEntity<BookingResponseDto> createBookingResponse = createBookingInDb();

        //WHEN
        ResponseEntity<BookingResponseWithCustomerDataDto> response = testRestTemplate.getForEntity(
                BASE_BOOKING_URL + "/" + Objects.requireNonNull(createBookingResponse.getBody()).getId(),
                BookingResponseWithCustomerDataDto.class
        );

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetBookingShouldThrowWhenBookingNotFound() {
        //WHEN
        ResponseEntity<BookingResponseDto> response = testRestTemplate.getForEntity(
                BASE_BOOKING_URL + "/" + UUID.randomUUID(),
                BookingResponseDto.class
        );

        //THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllBookingsSuccessfully() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        createBookingInDb();

        //WHEN
        ResponseEntity<List<BookingResponseWithCustomerDataDto>> response =
                this.testRestTemplate.exchange(
                        BASE_BOOKING_URL, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                        });

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetAllBookingsOfPropertySuccessfully() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        createBookingInDb();

        //WHEN
        ResponseEntity<List<BookingResponseDto>> response =
                this.testRestTemplate.exchange(
                        BASE_BOOKING_URL + "/property/" + propertyResponse.getBody().getId(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                        });

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testDeleteBookingSuccessfully() {
        //GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        ResponseEntity<BookingResponseDto> createBookingResponse = createBookingInDb();

        //WHEN
        ResponseEntity<BookingResponseDto> response = testRestTemplate.exchange(
                BASE_BOOKING_URL + "/" + Objects.requireNonNull(createBookingResponse.getBody()).getId(),
                HttpMethod.DELETE,
                null,
                BookingResponseDto.class
        );

        //THEN
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteBookingShouldThrowWhenBookingNotFound() {
        //WHEN
        ResponseEntity<BookingResponseDto> response = testRestTemplate.exchange(
                BASE_BOOKING_URL + "/" + UUID.randomUUID(),
                HttpMethod.DELETE,
                null,
                BookingResponseDto.class
        );

        //THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void addCustomerDataToBooking_ShouldSucceedAndSendEmail_WhenBookingExists() throws Exception {
        // GIVEN
        createUserInDb();
        ResponseEntity<PropertyResponseDto> propertyResponse = createPropertyInDb();
        bookingRequestDto.setPropertyId(Objects.requireNonNull(propertyResponse.getBody()).getId());
        ResponseEntity<BookingResponseDto> createBookingResponse = createBookingInDb();

        validBookingRequestCustomerDataDto.setBookingId(Objects.requireNonNull(createBookingResponse.getBody()).getId());

        // WHEN
        ResponseEntity<BookingResponseWithCustomerDataDto> response =
                testRestTemplate.exchange(
                        CUSTOMER_DATA_URL,
                        HttpMethod.POST,
                        new HttpEntity<>(validBookingRequestCustomerDataDto, new HttpHeaders()),
                        BookingResponseWithCustomerDataDto.class
                );

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Optional<Booking> updatedBooking = bookingRepository.findById(Objects.requireNonNull(response.getBody()).getBookingRequestCustomerDataDto()
                .getBookingId());
        assertTrue(updatedBooking.isPresent());
        assertNotNull(updatedBooking.get().getCustomer());

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        assertEquals(customer.getEmail(), receivedMessages[0].getAllRecipients()[0].toString());
    }

    @Test
    void addCustomerDataToBooking_ShouldThrowBookingNotFoundException_WhenBookingDoesNotExist()  {
        // GIVEN:

        // WHEN:
        ResponseEntity<String> response =
                testRestTemplate.exchange(
                        CUSTOMER_DATA_URL,
                        HttpMethod.POST,
                        new HttpEntity<>(invalidBookingRequestCustomerDataDto, new HttpHeaders()),
                        String.class
                );

        // THEN:
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Booking not found"));
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

    private BigDecimal calculateBookingPrice(BookingRequestDto bookingRequestDto, PropertyResponseDto property){
        int totalPropertyPrice = property.getPrice();

        if (bookingRequestDto.isPetContent()) {
            totalPropertyPrice += property.getPetPrice();
        }

        LocalDate startLocalDate = bookingRequestDto.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = bookingRequestDto.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long numberOfDays = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);

        long totalBookingPrice = numberOfDays * totalPropertyPrice;
        return new BigDecimal(totalBookingPrice);
    }
}