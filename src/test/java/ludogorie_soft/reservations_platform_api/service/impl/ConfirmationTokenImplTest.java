package ludogorie_soft.reservations_platform_api.service.impl;

import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.ConfirmationToken;
import ludogorie_soft.reservations_platform_api.entity.Customer;
import ludogorie_soft.reservations_platform_api.exception.BookingNotFoundException;
import ludogorie_soft.reservations_platform_api.exception.ConfirmationTokenNotFoundException;
import ludogorie_soft.reservations_platform_api.exception.ConfirmationTokenNotValidException;
import ludogorie_soft.reservations_platform_api.exception.CustomerNotFoundException;
import ludogorie_soft.reservations_platform_api.exception.ResourceNotFoundException;
import ludogorie_soft.reservations_platform_api.helper.BookingTestHelper;
import ludogorie_soft.reservations_platform_api.helper.ConfirmationTokenTestHelper;
import ludogorie_soft.reservations_platform_api.helper.CustomerTestHelper;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.ConfirmationTokenRepository;
import ludogorie_soft.reservations_platform_api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;


import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfirmationTokenImplTest {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ConfirmationTokenServiceImpl confirmationTokenService;

    private ConfirmationToken confirmationToken;
    private Booking booking;
    private Customer customer;


    @BeforeEach
    void setUp() {
        confirmationToken = ConfirmationTokenTestHelper.createConfirmationToken();
        customer = CustomerTestHelper.createCustomer();
        booking = BookingTestHelper.createBooking();

        booking.setCustomer(customer);
        booking.setConfirmationToken(confirmationToken);
    }

    @Test
    void testCreateConfirmationToken() {
        // GIVEN a new confirmation token
        when(confirmationTokenRepository.save(any(ConfirmationToken.class))).thenReturn(confirmationToken);

        // WHEN the createConfirmationToken method is called
        ConfirmationToken createdToken = confirmationTokenService.createConfirmationToken();

        // THEN a valid confirmation token should be created and saved
        assertNotNull(createdToken);
        assertNotNull(createdToken.getToken());
        assertNotNull(createdToken.getCreatedAt());
        assertNotNull(createdToken.getExpiresAt());
        verify(confirmationTokenRepository, times(1)).save(any(ConfirmationToken.class));
    }

    @Test
    void testResetConfirmationToken() {
        // GIVEN a valid customer and booking
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(bookingRepository.findByCustomerId(customer.getId())).thenReturn(Optional.of(booking));

        // WHEN the resetConfirmationToken method is called
        confirmationTokenService.resetConfirmationToken(customer.getId());

        // THEN the token should be updated and saved
        assertNotNull(confirmationToken.getToken());
        assertNotNull(confirmationToken.getCreatedAt());
        assertNotNull(confirmationToken.getExpiresAt());
        verify(confirmationTokenRepository, times(1)).save(confirmationToken);
    }

    @Test
    void testResetConfirmationToken_CustomerNotFound() {
        // GIVEN a non-existing customer ID
        when(customerRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN the resetConfirmationToken method is called
        // THEN it should throw a ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> confirmationTokenService.resetConfirmationToken(customer.getId()));
    }

    @Test
    void testResetConfirmationToken_BookingNotFound() {
        // GIVEN a customer with no associated booking
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(bookingRepository.findByCustomerId(customer.getId())).thenReturn(Optional.empty());

        // WHEN the resetConfirmationToken method is called
        // THEN it should throw a ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> confirmationTokenService.resetConfirmationToken(customer.getId()));
    }

    @Test
    void testConfirmReservation_ValidToken() {
        // GIVEN a valid confirmation token, booking, and customer
        when(confirmationTokenRepository.findByToken(confirmationToken.getToken()))
                .thenReturn(Optional.of(confirmationToken));
        when(bookingRepository.findByConfirmationTokenId(confirmationToken.getId()))
                .thenReturn(Optional.of(booking));
        when(customerRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));

        // WHEN the confirmReservation method is called
        BookingResponseWithCustomerDataDto result = confirmationTokenService.confirmReservation(confirmationToken.getToken());

        // THEN the token is confirmed, saved, and the correct response is returned
        assertNotNull(result);
        assertEquals(booking.getId(), result.getBookingId());
        assertEquals(customer.getFirstName(), result.getFirstName());

        verify(confirmationTokenRepository).save(confirmationToken);
    }

    @Test
    void testConfirmReservation_InvalidToken_NotFound() {
        // GIVEN a token that does not exist
        String invalidToken = "non-existent-token";
        when(confirmationTokenRepository.findByToken(invalidToken)).thenReturn(Optional.empty());

        // WHEN the confirmReservation method is called
        // THEN a ConfirmationTokenNotFoundException is thrown
        assertThrows(ConfirmationTokenNotFoundException.class, () ->
                confirmationTokenService.confirmReservation(invalidToken)
        );

        verify(confirmationTokenRepository, never()).save(any());
    }

    @Test
    void testConfirmReservation_InvalidToken_Expired() {
        // GIVEN an expired confirmation token
        ConfirmationToken expiredToken = ConfirmationTokenTestHelper.createExpiredConfirmationToken();
        when(confirmationTokenRepository.findByToken(expiredToken.getToken()))
                .thenReturn(Optional.of(expiredToken));
        when(bookingRepository.findByConfirmationTokenId(expiredToken.getId()))
                .thenReturn(Optional.of(booking));
        when(customerRepository.findById(booking.getCustomer().getId()))
                .thenReturn(Optional.of(customer));

        // WHEN the confirmReservation method is called
        // THEN a ConfirmationTokenNotValidException is thrown
        assertThrows(ConfirmationTokenNotValidException.class, () ->
                confirmationTokenService.confirmReservation(expiredToken.getToken())
        );

        verify(confirmationTokenRepository, never()).save(expiredToken);
    }

    @Test
    void testConfirmReservation_BookingNotFound() {
        // GIVEN a valid confirmation token but no associated booking
        when(confirmationTokenRepository.findByToken(confirmationToken.getToken()))
                .thenReturn(Optional.of(confirmationToken));
        when(bookingRepository.findByConfirmationTokenId(confirmationToken.getId()))
                .thenReturn(Optional.empty());

        // WHEN the confirmReservation method is called
        // THEN a BookingNotFoundException is thrown
        assertThrows(BookingNotFoundException.class, () ->
                confirmationTokenService.confirmReservation(confirmationToken.getToken())
        );

        verify(confirmationTokenRepository, never()).save(any());
    }

    @Test
    void testConfirmReservation_CustomerNotFound() {
        // GIVEN a valid confirmation token and booking but no associated customer
        when(confirmationTokenRepository.findByToken(confirmationToken.getToken()))
                .thenReturn(Optional.of(confirmationToken));
        when(bookingRepository.findByConfirmationTokenId(confirmationToken.getId()))
                .thenReturn(Optional.of(booking));
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());

        // WHEN the confirmReservation method is called
        // THEN a CustomerNotFoundException is thrown
        assertThrows(CustomerNotFoundException.class, () ->
                confirmationTokenService.confirmReservation(confirmationToken.getToken())
        );

        verify(confirmationTokenRepository, never()).save(any());
    }

}
