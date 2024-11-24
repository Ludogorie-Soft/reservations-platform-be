package ludogorie_soft.reservations_platform_api.controller;

import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.ConfirmationToken;
import ludogorie_soft.reservations_platform_api.entity.Customer;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.helper.BookingTestHelper;
import ludogorie_soft.reservations_platform_api.helper.ConfirmationTokenTestHelper;
import ludogorie_soft.reservations_platform_api.helper.CustomerTestHelper;
import ludogorie_soft.reservations_platform_api.helper.PropertyTestHelper;
import ludogorie_soft.reservations_platform_api.helper.UserTestHelper;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.ConfirmationTokenRepository;
import ludogorie_soft.reservations_platform_api.repository.CustomerRepository;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.repository.RoleRepository;
import ludogorie_soft.reservations_platform_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"test"})
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false"
})
class ConfirmationTokenControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private UUID customerId;
    private ConfirmationToken confirmationToken;
    private Booking booking;
    private Property property;
    private User user;
    private Customer customer;
    private boolean useSetup = true;

    @BeforeEach
    void setUp() {
        if (!useSetup) {
            return;
        }

        user = UserTestHelper.createUserForIntegrationTest();
        userRepository.save(user);

        property = PropertyTestHelper.createPropertyForIntegrationTest();
        property.setOwner(user);
        propertyRepository.save(property);

        customer = CustomerTestHelper.createCustomerForIntegrationTest();
        customerRepository.save(customer);
        customerId = customer.getId();

        confirmationToken = ConfirmationTokenTestHelper.createConfirmationTokenForIntegrationTest();
        confirmationTokenRepository.save(confirmationToken);

        booking = BookingTestHelper.createBookingForIntegrationTest();
        booking.setProperty(property);
        booking.setTotalPrice(BigDecimal.valueOf(property.getPrice()));
        booking.setConfirmationToken(confirmationToken);
        booking.setCustomer(customer);
        bookingRepository.save(booking);

    }

    @Test
    @DisplayName("Should confirm reservation with valid token")
    void confirmReservation_ShouldReturnBookingResponseWithCustomerData_WhenTokenIsValid() throws Exception {
        mockMvc.perform(get("/api/confirmation-tokens/confirm/{token}", confirmationToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingResponseDto.id").value(booking.getId().toString()))
                .andExpect(jsonPath("$.bookingRequestCustomerDataDto.firstName").value(booking.getCustomer().getFirstName()))
                .andExpect(jsonPath("$.bookingRequestCustomerDataDto.lastName").value(booking.getCustomer().getLastName()))
                .andExpect(jsonPath("$.bookingRequestCustomerDataDto.email").value(booking.getCustomer().getEmail()))
                .andExpect(jsonPath("$.bookingRequestCustomerDataDto.phoneNumber").value(booking.getCustomer().getPhoneNumber()));
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when token is expired")
    void confirmReservation_ShouldReturnBadRequest_WhenTokenIsExpired() throws Exception {
        useSetup = false;

        User expiredUser = UserTestHelper.createExpiredUserForIntegrationTest();
        userRepository.save(expiredUser);

        Property expiredProperty = PropertyTestHelper.createExpiredPropertyForIntegrationTest();
        expiredProperty.setOwner(expiredUser);
        propertyRepository.save(expiredProperty);

        ConfirmationToken expiredToken = ConfirmationTokenTestHelper.createExpiredConfirmationTokenForIntegrationTest();
        confirmationTokenRepository.save(expiredToken);

        Customer expiredCustomer = CustomerTestHelper.createExpiredCustomerForIntegrationTest();
        customerRepository.save(expiredCustomer);

        Booking expiredBooking = BookingTestHelper.createExpiredBookingForIntegrationTest();
        expiredBooking.setCustomer(expiredCustomer);
        expiredBooking.setConfirmationToken(expiredToken);
        expiredBooking.setProperty(expiredProperty);
        expiredBooking.setTotalPrice(BigDecimal.valueOf(expiredProperty.getPrice()));
        bookingRepository.save(expiredBooking);

        mockMvc.perform(get("/api/confirmation-tokens/confirm/{token}", expiredToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Token is expired"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when token is invalid")
    void confirmReservation_ShouldReturnNotFound_WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/api/confirmation-tokens/confirm/{token}", "invalid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Token not found with token: invalid-token"));
    }

    @Test
    @DisplayName("Should resend confirmation link for valid customer")
    void resendConfirmation_ShouldReturnSuccessMessage_WhenCustomerExists() throws Exception {
        mockMvc.perform(post("/api/confirmation-tokens/resend-confirmation/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Confirmation link resent!"));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when customer does not exist")
    void resendConfirmation_ShouldReturnNotFound_WhenCustomerDoesNotExist() throws Exception {
        UUID invalidCustomerId = UUID.randomUUID();
        mockMvc.perform(post("/api/confirmation-tokens/resend-confirmation/{customerId}", invalidCustomerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found with id: " + invalidCustomerId));

    }
}