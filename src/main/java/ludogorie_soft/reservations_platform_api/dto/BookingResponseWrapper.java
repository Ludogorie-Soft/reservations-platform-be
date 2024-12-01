package ludogorie_soft.reservations_platform_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingResponseWrapper {
    private BookingResponseWithCustomerDataDto bookingResponseWithCustomerData;
    private boolean alreadyConfirmed;
}

