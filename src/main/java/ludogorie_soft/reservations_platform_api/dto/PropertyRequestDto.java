package ludogorie_soft.reservations_platform_api.dto;

import lombok.Data;

@Data
public class PropertyRequestDto {

    private String name;
    private String type;
    private String ownersEmail;

}
