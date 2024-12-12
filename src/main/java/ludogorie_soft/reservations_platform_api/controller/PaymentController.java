package ludogorie_soft.reservations_platform_api.controller;

import lombok.AllArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.service.BookingService;
import ludogorie_soft.reservations_platform_api.service.PaymentService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return paymentService.createPaymentIntent(String.valueOf(booking.getTotalPrice()));
    }
}
