package ludogorie_soft.reservations_platform_api.dto;

import lombok.Data;

@Data
public class BookingResponseWithCustomerDataDto {

    private BookingResponseDto bookingResponseDto;
    private BookingRequestCustomerDataDto bookingRequestCustomerDataDto;

}
