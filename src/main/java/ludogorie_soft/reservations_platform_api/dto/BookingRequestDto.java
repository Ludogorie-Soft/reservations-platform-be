package ludogorie_soft.reservations_platform_api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BookingRequestDto {

    private Long propertyId;
    private String email;
    private Date startDate;
    private Date endDate;
    private String description;
}
