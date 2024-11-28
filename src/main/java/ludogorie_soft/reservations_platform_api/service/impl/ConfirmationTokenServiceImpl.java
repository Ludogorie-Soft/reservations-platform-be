package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWrapper;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.ConfirmationToken;
import ludogorie_soft.reservations_platform_api.entity.Customer;
import ludogorie_soft.reservations_platform_api.exception.BookingNotFoundException;
import ludogorie_soft.reservations_platform_api.exception.ConfirmationTokenAlreadyConfirmedException;
import ludogorie_soft.reservations_platform_api.exception.ConfirmationTokenExpiredException;
import ludogorie_soft.reservations_platform_api.exception.ConfirmationTokenNotFoundException;
import ludogorie_soft.reservations_platform_api.exception.CustomerNotFoundException;
import ludogorie_soft.reservations_platform_api.exception.ResourceNotFoundException;
import ludogorie_soft.reservations_platform_api.mapper.BookingResponseWithCustomerDataMapper;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.ConfirmationTokenRepository;
import ludogorie_soft.reservations_platform_api.repository.CustomerRepository;
import ludogorie_soft.reservations_platform_api.service.ConfirmationTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ConfirmationToken createConfirmationToken() {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken(UUID.randomUUID().toString());
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        confirmationTokenRepository.save(confirmationToken);
        return confirmationToken;
    }

    @Override
    public void resetConfirmationToken(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        Booking booking = bookingRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        ConfirmationToken confirmationToken = booking.getConfirmationToken();

        confirmationToken.setToken(UUID.randomUUID().toString());
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    @Transactional
    public BookingResponseWrapper  confirmReservation(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ConfirmationTokenNotFoundException("Token not found with token: " + token));
        Booking booking = bookingRepository.findByConfirmationTokenId(confirmationToken.getId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with token: " + token));
        Customer customer = customerRepository.findById(booking.getCustomer().getId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with booking: " + booking.getId()));

        if (isTokenNotExpired(confirmationToken)) {
            if (isTokenNotConfirmed(confirmationToken)) {
                confirmationToken.setConfirmedAt(LocalDateTime.now());
                confirmationTokenRepository.save(confirmationToken);
                return new BookingResponseWrapper(BookingResponseWithCustomerDataMapper.toBookingWithCustomerDataDto(booking, customer),
                        false
                );
            } else {
                return new BookingResponseWrapper(
                        BookingResponseWithCustomerDataMapper.toBookingWithCustomerDataDto(booking, customer),
                        true
                );
            }
        } else {
            throw new ConfirmationTokenExpiredException("Token is expired");
        }
    }

    private boolean isTokenNotExpired(ConfirmationToken confirmationToken) {
        LocalDateTime now = LocalDateTime.now();
        return !confirmationToken.getExpiresAt().isBefore(now);
    }

    private boolean isTokenNotConfirmed(ConfirmationToken confirmationToken) {
        return confirmationToken.getConfirmedAt() == null;
    }

}
