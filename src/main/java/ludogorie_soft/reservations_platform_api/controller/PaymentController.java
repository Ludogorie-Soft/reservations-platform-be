package ludogorie_soft.reservations_platform_api.controller;
import lombok.AllArgsConstructor;
import ludogorie_soft.reservations_platform_api.service.PaymentService;
import ludogorie_soft.reservations_platform_api.service.impl.PaymentServiceImpl;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@AllArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping("/create-payment-intent")
    public Map<String, Object> createPaymentIntent(@RequestBody Map<String, String> request) {
        String itemId = request.get("itemId");
        return paymentService.createPaymentIntent(itemId);
    }
}
