package ludogorie_soft.reservations_platform_api.controller;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestCustomerDataDto;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.service.BookingService;
import ludogorie_soft.reservations_platform_api.service.ConfirmationTokenService;
import net.fortuna.ical4j.data.ParserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ConfirmationTokenService confirmationTokenService;

    @PostMapping
    ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingRequestDto bookingRequestDto) throws IOException, URISyntaxException, ParserException {
        BookingResponseDto response = bookingService.createBooking(bookingRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/customer-data")
    ResponseEntity<BookingResponseWithCustomerDataDto> addCustomerDataToBooking(@RequestBody BookingRequestCustomerDataDto bookingRequestCustomerDataDto) {
        BookingResponseWithCustomerDataDto response = bookingService.addCustomerDataToBooking(bookingRequestCustomerDataDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

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

    @PutMapping("/{id}")
    ResponseEntity<BookingResponseDto> editBooking(@PathVariable("id") UUID id, @RequestBody BookingRequestDto bookingRequestDto) throws ParserException, IOException {
        BookingResponseDto response = bookingService.editBooking(id, bookingRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        List<BookingResponseDto> bookings = bookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/property/{id}")
    ResponseEntity<List<BookingResponseDto>> getAllBookingsOfProperty(@PathVariable("id") UUID id) {
        List<BookingResponseDto> bookings = bookingService.getAllBookingsOfProperty(id);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    ResponseEntity<BookingResponseDto> getBooking(@PathVariable("id") UUID id) {
        BookingResponseDto bookings = bookingService.getBooking(id);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<BookingResponseDto> deleteBooking(@PathVariable("id") UUID id) {
        BookingResponseDto response = bookingService.deleteBooking(id);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}
