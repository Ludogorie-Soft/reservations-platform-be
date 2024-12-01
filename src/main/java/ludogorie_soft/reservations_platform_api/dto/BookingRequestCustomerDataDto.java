package ludogorie_soft.reservations_platform_api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BookingRequestCustomerDataDto {

    private UUID bookingId;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
