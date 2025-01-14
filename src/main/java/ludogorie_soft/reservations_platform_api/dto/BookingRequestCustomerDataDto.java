package ludogorie_soft.reservations_platform_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class BookingRequestCustomerDataDto {

    @NotNull(message = "Booking ID cannot be null")
    private UUID bookingId;

    @Size(min = 2,max = 32, message = "Invalid first name")
    private String firstName;

    @Size(min = 2, max = 32, message = "Invalid last name")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "\\+?[0-9\\-]+", message = "Invalid phone number characters")
    @Size(min = 3, max = 15, message = "Invalid phone number length")
    private String phoneNumber;

    @NotBlank(message = "Reservation notes cannot be blank")
    @Size(min = 1, max = 1500, message = "Invalid reservation note length")
    private String reservationNotes;

}
