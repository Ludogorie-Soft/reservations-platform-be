package ludogorie_soft.reservations_platform_api.dto;

import lombok.Data;

@Data
public class UserResponseDto {

    private Long id;
    private String email;
    private String name;
    private String username;
}
