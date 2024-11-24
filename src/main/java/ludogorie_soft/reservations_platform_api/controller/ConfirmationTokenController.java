package ludogorie_soft.reservations_platform_api.controller;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.service.ConfirmationTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/confirmation-tokens")
public class ConfirmationTokenController {

    private final ConfirmationTokenService confirmationTokenService;

    @GetMapping("/confirm/{token}")
    ResponseEntity<BookingResponseWithCustomerDataDto> confirmReservation(@PathVariable String token) {
        BookingResponseWithCustomerDataDto response = confirmationTokenService.confirmReservation(token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/resend-confirmation/{customerId}")
    ResponseEntity<String> resendConfirmation(@PathVariable UUID customerId) {
        confirmationTokenService.resetConfirmationToken(customerId);
        return ResponseEntity.ok("Confirmation link resent!");
    }
}
