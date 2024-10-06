package ludogorie_soft.reservations_platform_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class PropertyResponseDto {

    private Long id;
    private UserResponseDto owner;
    private String name;
    private String type;
    private String airBnbUrl;
    private String bookingUrl;
    private List<BookingResponseDto> bookings;
    private String syncUrl;
    private String websiteUrl;
    private int capacity;
    private boolean isPetAllowed;
    private String petRules;
    private int price;
}