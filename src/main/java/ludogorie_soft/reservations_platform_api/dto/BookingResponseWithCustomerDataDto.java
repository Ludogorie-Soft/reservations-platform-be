package ludogorie_soft.reservations_platform_api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BookingResponseWithCustomerDataDto {

    private UUID id;
    private String startDate;
    private String endDate;
    private String description;
    int adultCount;
    int childrenCount;
    int babiesCount;
    boolean petContent;
    BigDecimal totalPrice;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
