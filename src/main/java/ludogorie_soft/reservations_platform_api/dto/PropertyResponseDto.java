package ludogorie_soft.reservations_platform_api.dto;

import jakarta.persistence.Lob;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PropertyResponseDto {

    private UUID id;
    private UserResponseDto owner;
    private String airBnbUrl;
    private String bookingUrl;
    private List<BookingResponseDto> bookings;
    private String syncUrl;
    private String websiteUrl;
    private int capacity;
    private boolean isPetAllowed;
    private String petRules;
    private int price;
    private int minimumStay;
    private int petPrice;
    @Lob
    private String propertyRules;
    private String publicKey;
    private String secretKey;
}
