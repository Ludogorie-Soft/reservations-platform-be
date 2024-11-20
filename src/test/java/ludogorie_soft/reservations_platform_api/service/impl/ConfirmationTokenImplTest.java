package ludogorie_soft.reservations_platform_api.service.impl;

import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.ConfirmationToken;
import ludogorie_soft.reservations_platform_api.entity.Customer;
import ludogorie_soft.reservations_platform_api.exception.BookingNotFoundException;
import ludogorie_soft.reservations_platform_api.exception.ConfirmationTokenExpiredException;
import ludogorie_soft.reservations_platform_api.exception.ConfirmationTokenNotFoundException;
import ludogorie_soft.reservations_platform_api.exception.ConfirmationTokenAlreadyConfirmedException;
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
class ConfirmationTokenImplTest {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BookingRepository bookingRepository;

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
        // GIVEN
        when(confirmationTokenRepository.save(any(ConfirmationToken.class))).thenReturn(confirmationToken);

        // WHEN
        ConfirmationToken createdToken = confirmationTokenService.createConfirmationToken();

        // THEN
        assertNotNull(createdToken);
        assertNotNull(createdToken.getToken());
        assertNotNull(createdToken.getCreatedAt());
        assertNotNull(createdToken.getExpiresAt());
        verify(confirmationTokenRepository, times(1)).save(any(ConfirmationToken.class));
    }

    @Test
    void testResetConfirmationToken() {
        // GIVEN
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(bookingRepository.findByCustomerId(customer.getId())).thenReturn(Optional.of(booking));

        // WHEN
        confirmationTokenService.resetConfirmationToken(customer.getId());

        // THEN
        assertNotNull(confirmationToken.getToken());
        assertNotNull(confirmationToken.getCreatedAt());
        assertNotNull(confirmationToken.getExpiresAt());
        verify(confirmationTokenRepository, times(1)).save(confirmationToken);
    }

    @Test
    void testResetConfirmationToken_CustomerNotFound() {
        // GIVEN
        when(customerRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        // THEN
        assertThrows(ResourceNotFoundException.class, () -> confirmationTokenService.resetConfirmationToken(customer.getId()));
    }

    @Test
    void testResetConfirmationToken_BookingNotFound() {
        // GIVEN
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(bookingRepository.findByCustomerId(customer.getId())).thenReturn(Optional.empty());

        // WHEN
        // THEN
        assertThrows(ResourceNotFoundException.class, () -> confirmationTokenService.resetConfirmationToken(customer.getId()));
    }

    @Test
    void testConfirmReservation_ValidToken() {
        // GIVEN
        when(confirmationTokenRepository.findByToken(confirmationToken.getToken()))
                .thenReturn(Optional.of(confirmationToken));
        when(bookingRepository.findByConfirmationTokenId(confirmationToken.getId()))
                .thenReturn(Optional.of(booking));
        when(customerRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));

        // WHEN
        BookingResponseWithCustomerDataDto result = confirmationTokenService.confirmReservation(confirmationToken.getToken());

        // THEN
        assertNotNull(result);
        assertEquals(booking.getId(), result.getBookingId());
        assertEquals(customer.getFirstName(), result.getFirstName());

        verify(confirmationTokenRepository).save(confirmationToken);
    }

    @Test
    void testConfirmReservation_InvalidToken_NotFound() {
        // GIVEN
        String invalidToken = "non-existent-token";
        when(confirmationTokenRepository.findByToken(invalidToken)).thenReturn(Optional.empty());

        // WHEN
        // THEN
        assertThrows(ConfirmationTokenNotFoundException.class, () ->
                confirmationTokenService.confirmReservation(invalidToken)
        );

        verify(confirmationTokenRepository, never()).save(any());
    }

    @Test
    void testConfirmReservation_InvalidToken_Expired() {
        // GIVEN
        ConfirmationToken expiredToken = ConfirmationTokenTestHelper.createExpiredConfirmationToken();
        when(confirmationTokenRepository.findByToken(expiredToken.getToken()))
                .thenReturn(Optional.of(expiredToken));
        when(bookingRepository.findByConfirmationTokenId(expiredToken.getId()))
                .thenReturn(Optional.of(booking));
        when(customerRepository.findById(booking.getCustomer().getId()))
                .thenReturn(Optional.of(customer));

        // WHEN
        // THEN
        assertThrows(ConfirmationTokenExpiredException.class, () ->
                confirmationTokenService.confirmReservation(expiredToken.getToken())
        );

        verify(confirmationTokenRepository, never()).save(expiredToken);
    }

    @Test
    void testConfirmReservation_InvalidToken_AlreadyConfirmed() {
        // GIVEN
        confirmationToken.setConfirmedAt(confirmationToken.getCreatedAt().plusMinutes(5));
        when(confirmationTokenRepository.findByToken(confirmationToken.getToken()))
                .thenReturn(Optional.of(confirmationToken));
        when(bookingRepository.findByConfirmationTokenId(confirmationToken.getId()))
                .thenReturn(Optional.of(booking));
        when(customerRepository.findById(booking.getCustomer().getId()))
                .thenReturn(Optional.of(customer));

        // WHEN
        // THEN
        assertThrows(ConfirmationTokenAlreadyConfirmedException.class, () ->
                confirmationTokenService.confirmReservation(confirmationToken.getToken())
        );

        verify(confirmationTokenRepository, never()).save(confirmationToken);
    }

    @Test
    void testConfirmReservation_BookingNotFound() {
        // GIVEN
        when(confirmationTokenRepository.findByToken(confirmationToken.getToken()))
                .thenReturn(Optional.of(confirmationToken));
        when(bookingRepository.findByConfirmationTokenId(confirmationToken.getId()))
                .thenReturn(Optional.empty());

        // WHEN
        // THEN
        assertThrows(BookingNotFoundException.class, () ->
                confirmationTokenService.confirmReservation(confirmationToken.getToken())
        );

        verify(confirmationTokenRepository, never()).save(any());
    }

    @Test
    void testConfirmReservation_CustomerNotFound() {
        // GIVEN
        when(confirmationTokenRepository.findByToken(confirmationToken.getToken()))
                .thenReturn(Optional.of(confirmationToken));
        when(bookingRepository.findByConfirmationTokenId(confirmationToken.getId()))
                .thenReturn(Optional.of(booking));
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());

        // WHEN
        // THEN
        assertThrows(CustomerNotFoundException.class, () ->
                confirmationTokenService.confirmReservation(confirmationToken.getToken())
        );

        verify(confirmationTokenRepository, never()).save(any());
    }

}
