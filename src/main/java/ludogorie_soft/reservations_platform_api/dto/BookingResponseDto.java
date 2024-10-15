package ludogorie_soft.reservations_platform_api.dto;

import lombok.Data;

@Data
public class BookingResponseDto {

    private Long id;
    private String startDate;
    private String endDate;
    private String description;
}
