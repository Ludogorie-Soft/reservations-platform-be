package ludogorie_soft.reservations_platform_api.dto;

import lombok.Data;

@Data
public class ReservationResponseDto {

    private Long id;
    private String startDate;
    private String endDate;
    private String description;

}
