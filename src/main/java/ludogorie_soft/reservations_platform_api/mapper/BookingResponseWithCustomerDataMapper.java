package ludogorie_soft.reservations_platform_api.mapper;

import lombok.NoArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Customer;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class BookingResponseWithCustomerDataMapper {

    public static BookingResponseWithCustomerDataDto toBookingWithCustomerDataDto (Booking booking, Customer customer) {
        BookingResponseWithCustomerDataDto dto = new BookingResponseWithCustomerDataDto();

        dto.setBookingId(booking.getId());
        dto.setStartDate(booking.getStartDate().toString());
        dto.setEndDate(booking.getEndDate().toString());
        dto.setDescription(booking.getDescription());
        dto.setAdultCount(booking.getAdultCount());
        dto.setChildrenCount(booking.getChildrenCount());
        dto.setBabiesCount(booking.getBabiesCount());
        dto.setPetContent(booking.isPetContent());
        dto.setTotalPrice(booking.getTotalPrice());

        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());

        return dto;
    }

}
