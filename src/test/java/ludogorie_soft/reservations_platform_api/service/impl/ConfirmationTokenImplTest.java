package ludogorie_soft.reservations_platform_api.service.impl;

import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.ConfirmationToken;
import ludogorie_soft.reservations_platform_api.entity.Customer;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

        customer.setBooking(booking);
    }

    @Test
    void createConfirmationToken_ShouldReturnNewToken() {
        // GIVEN
        when(confirmationTokenRepository.save(any(ConfirmationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        ConfirmationToken result = confirmationTokenService.createConfirmationToken();

        // THEN
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertTrue(result.getExpiresAt().isAfter(result.getCreatedAt()));
        verify(confirmationTokenRepository, times(1)).save(any(ConfirmationToken.class));
    }

    @Test
    void resetConfirmationToken_ShouldUpdateToken() {
        // GIVEN
        UUID customerId = customer.getId();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(confirmationTokenRepository.save(any(ConfirmationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        confirmationTokenService.resetConfirmationToken(customerId);

        // THEN
        verify(customerRepository, times(1)).findById(customerId);
        verify(confirmationTokenRepository, times(1)).save(any(ConfirmationToken.class));
        assertNotNull(customer.getBooking().getConfirmationToken().getToken());
    }

    @Test
    void resetConfirmationToken_CustomerNotFound_ShouldThrowException() {
        // GIVEN
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // WHEN
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            confirmationTokenService.resetConfirmationToken(customerId);
        });

        // THEN
        assertEquals("Customer not found with id: " + customerId, exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
        verify(confirmationTokenRepository, times(0)).save(any(ConfirmationToken.class));
    }

    @Test
    void getToken_ShouldReturnConfirmationToken() {
        // GIVEN
        String token = confirmationToken.getToken();
        when(confirmationTokenRepository.findByToken(token)).thenReturn(Optional.of(confirmationToken));

        // WHEN
        ConfirmationToken result = confirmationTokenService.getToken(token);

        // THEN
        assertEquals(confirmationToken, result);
        verify(confirmationTokenRepository, times(1)).findByToken(token);
    }

    @Test
    void getToken_TokenNotFound_ShouldThrowException() {
        // GIVEN
        String token = "invalid-token";
        when(confirmationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // WHEN
        Exception exception = assertThrows(RuntimeException.class, () -> {
            confirmationTokenService.getToken(token);
        });

        // THEN
        assertEquals("Token not found!", exception.getMessage());
        verify(confirmationTokenRepository, times(1)).findByToken(token);
    }

    @Test
    void confirmReservation_ValidToken_ShouldReturnBookingResponse() {
        // GIVEN
        String token = confirmationToken.getToken();
        BookingResponseWithCustomerDataDto responseDto = new BookingResponseWithCustomerDataDto();
        when(confirmationTokenRepository.findByToken(token)).thenReturn(Optional.of(confirmationToken));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(modelMapper.map(booking, BookingResponseWithCustomerDataDto.class)).thenReturn(responseDto);

        // WHEN
        BookingResponseWithCustomerDataDto result = confirmationTokenService.confirmReservation(token);

        // THEN
        assertEquals(responseDto, result);
        verify(confirmationTokenRepository, times(1)).findByToken(token);
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(confirmationTokenRepository, times(1)).save(confirmationToken);
        assertNotNull(confirmationToken.getConfirmedAt());
    }

    @Test
    void confirmReservation_TokenExpired_ShouldThrowException() {
        // GIVEN
        ConfirmationToken expiredToken = ConfirmationTokenTestHelper.createExpiredConfirmationToken();
        booking.setConfirmationToken(expiredToken);
        when(confirmationTokenRepository.findByToken(expiredToken.getToken())).thenReturn(Optional.of(expiredToken));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // WHEN
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            confirmationTokenService.confirmReservation(expiredToken.getToken());
        });

        // THEN
        assertEquals("Token is expired or already confirmed!", exception.getMessage());
        verify(confirmationTokenRepository, times(1)).findByToken(expiredToken.getToken());
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

}
