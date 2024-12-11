package ludogorie_soft.reservations_platform_api.controller;

import lombok.AllArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.exception.BookingNotFoundException;
import ludogorie_soft.reservations_platform_api.service.BookingService;
import ludogorie_soft.reservations_platform_api.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;

    @PostMapping("/create-payment-intent/{bookingId}")
    public Map<String, Object> createPaymentIntent(@PathVariable UUID bookingId) {
        BookingResponseDto booking = bookingService.getBooking(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException("Booking with id " + bookingId + " not found!");
        }
        if (booking.getTotalPrice() == null || booking.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total price is missing or invalid for booking id " + bookingId);
        }
        return paymentService.createPaymentIntent(String.valueOf(booking.getTotalPrice()));
    }

}
