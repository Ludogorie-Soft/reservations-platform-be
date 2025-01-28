package ludogorie_soft.reservations_platform_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Lob;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class PropertyRequestDto {

    @NotBlank(message = "Owner email is required")
    @Email(message = "Please provide a valid email")
    private String ownerEmail;

    @NotBlank(message = "Website URL is required")
    @URL
    private String websiteUrl;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

    private boolean isPetAllowed;

    private String petRules;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private int price;
    private int minimumStay;
    private int petPrice;
    @Lob
    private String propertyRules;
    private String publicKey;
    private String secretKey;
}
