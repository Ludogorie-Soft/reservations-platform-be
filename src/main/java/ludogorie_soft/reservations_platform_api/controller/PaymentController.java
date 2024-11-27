package ludogorie_soft.reservations_platform_api.controller;

import lombok.AllArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.service.BookingService;
import ludogorie_soft.reservations_platform_api.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;

    //    @PostMapping("/create-payment-intent")
//    public Map<String, Object> createPaymentIntent(@RequestBody Map<String, String> request) {
//        String itemId = request.get("itemId");
//        return paymentService.createPaymentIntent(itemId);
//    }
    @PostMapping("/create-payment-intent/{bookingId}")
    public Map<String, Object> createPaymentIntent(@PathVariable UUID bookingId) {
        BookingResponseDto booking = bookingService.getBooking(bookingId);
        if (booking == null || booking.getTotalPrice() == null) {
            throw new IllegalArgumentException("Invalid booking or total price");
        }
        return paymentService.createPaymentIntent(String.valueOf(booking.getTotalPrice()));
    }

//    @PostMapping("/confirm-payment")
//    public Map<String, Object> confirmPayment(@RequestBody Map<String, String> request) {
//        String paymentIntentId = request.get("paymentIntentId");
//        return paymentService.confirmPayment(paymentIntentId);
//    }
}
