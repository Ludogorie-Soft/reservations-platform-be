package ludogorie_soft.reservations_platform_api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class BookingRequestDto {

    @NotNull(message = "Property ID cannot be null")
    private UUID propertyId;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private Date startDate;

    @NotNull(message = "End date cannot be null")
    @FutureOrPresent(message = "End date cannot be in the past")
    private Date endDate;

    @Min(value = 1, message = "At least one adult is required")
    private int adultCount;

    @Min(value = 0, message = "Children count cannot be negative")
    private int childrenCount;

    @Min(value = 0, message = "Babies count cannot be negative")
    private int babiesCount;

    private boolean petContent;
}
