package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.ConfirmationToken;
import ludogorie_soft.reservations_platform_api.entity.Customer;
import ludogorie_soft.reservations_platform_api.exception.ResourceNotFoundException;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.ConfirmationTokenRepository;
import ludogorie_soft.reservations_platform_api.repository.CustomerRepository;
import ludogorie_soft.reservations_platform_api.service.ConfirmationTokenService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    @Override
    public ConfirmationToken createConfirmationToken() {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationTokenRepository.save(confirmationToken);
        return confirmationToken;
    }

    @Override
    public void resetConfirmationToken(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));

        ConfirmationToken confirmationToken = customer.getBooking().getConfirmationToken();

        confirmationToken.setToken(UUID.randomUUID().toString());
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        confirmationTokenRepository.save(confirmationToken);
    }

//    @Override
//    public ConfirmationToken resetConfirmationToken(ConfirmationToken resetConfirmationToken) {
//        confirmationTokenRepository.findByToken(resetConfirmationToken.getToken()).orElseThrow(() -> new RuntimeException("Token not found!"));
//
//        resetConfirmationToken.setToken(UUID.randomUUID().toString());
//        resetConfirmationToken.setCreatedAt(LocalDateTime.now());
//        resetConfirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
//
//        confirmationTokenRepository.save(resetConfirmationToken);
//        return resetConfirmationToken;
//    }

    @Override
    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Token not found!"));
    }

    @Override
    public BookingResponseWithCustomerDataDto confirmReservation(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Token not found!"));

        Booking booking = bookingRepository.findById(confirmationToken.getBooking().getId()).orElseThrow(() -> new ResourceNotFoundException("Booking not found!"));

        if (isTokenValid(confirmationToken)) {
            confirmationToken.setConfirmedAt(LocalDateTime.now());
            confirmationTokenRepository.save(confirmationToken);
            return modelMapper.map(booking, BookingResponseWithCustomerDataDto.class);
        } else {
            throw new ResourceNotFoundException("Token is expired or already confirmed!");
        }
    }

    private boolean isTokenValid(ConfirmationToken confirmationToken) {
        LocalDateTime now = LocalDateTime.now();

        if (confirmationToken.getExpiresAt().isBefore(now)) {
            return false;
        }
        if (confirmationToken.getConfirmedAt() != null) {
            return false;
        }

        return true;
    }

}
