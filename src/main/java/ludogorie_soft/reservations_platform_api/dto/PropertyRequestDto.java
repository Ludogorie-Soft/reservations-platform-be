package ludogorie_soft.reservations_platform_api.dto;

import lombok.Data;

@Data
public class PropertyRequestDto {

    private String ownerEmail;
    private String websiteUrl;
    private int capacity;
    private boolean isPetAllowed;
    private String petRules;
    private int price;
    private int minimumStay;

}
