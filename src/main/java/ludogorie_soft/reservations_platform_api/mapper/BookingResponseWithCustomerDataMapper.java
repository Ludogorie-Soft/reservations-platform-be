package ludogorie_soft.reservations_platform_api.mapper;

import lombok.NoArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestCustomerDataDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Customer;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class BookingResponseWithCustomerDataMapper {

    public static BookingResponseWithCustomerDataDto toBookingWithCustomerDataDto(Booking booking, Customer customer) {
        BookingResponseWithCustomerDataDto dto = new BookingResponseWithCustomerDataDto();
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        BookingRequestCustomerDataDto bookingRequestCustomerDataDto = new BookingRequestCustomerDataDto();

        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStartDate(booking.getStartDate().toString());
        bookingResponseDto.setEndDate(booking.getEndDate().toString());
        bookingResponseDto.setDescription(booking.getDescription());
        bookingResponseDto.setAdultCount(booking.getAdultCount());
        bookingResponseDto.setChildrenCount(booking.getChildrenCount());
        bookingResponseDto.setBabiesCount(booking.getBabiesCount());
        bookingResponseDto.setPetContent(booking.isPetContent());
        bookingResponseDto.setTotalPrice(booking.getTotalPrice());

        bookingRequestCustomerDataDto.setBookingId(booking.getId());
        bookingRequestCustomerDataDto.setFirstName(customer.getFirstName());
        bookingRequestCustomerDataDto.setLastName(customer.getLastName());
        bookingRequestCustomerDataDto.setEmail(customer.getEmail());
        bookingRequestCustomerDataDto.setPhoneNumber(customer.getPhoneNumber());

        dto.setBookingResponseDto(bookingResponseDto);
        dto.setBookingRequestCustomerDataDto(bookingRequestCustomerDataDto);

        return dto;
    }

    public static BookingResponseWithCustomerDataDto toBookingWithCustomerDataDto(Booking booking) {
        BookingResponseWithCustomerDataDto dto = new BookingResponseWithCustomerDataDto();
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        BookingRequestCustomerDataDto bookingRequestCustomerDataDto = new BookingRequestCustomerDataDto();

        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStartDate(booking.getStartDate().toString());
        bookingResponseDto.setEndDate(booking.getEndDate().toString());
        bookingResponseDto.setDescription(booking.getDescription());
        bookingResponseDto.setAdultCount(booking.getAdultCount());
        bookingResponseDto.setChildrenCount(booking.getChildrenCount());
        bookingResponseDto.setBabiesCount(booking.getBabiesCount());
        bookingResponseDto.setPetContent(booking.isPetContent());
        bookingResponseDto.setTotalPrice(booking.getTotalPrice());

        bookingRequestCustomerDataDto.setBookingId(booking.getId());
        if (booking.getCustomer() != null) {
            bookingRequestCustomerDataDto.setFirstName(booking.getCustomer().getFirstName());
            bookingRequestCustomerDataDto.setLastName(booking.getCustomer().getLastName());
            bookingRequestCustomerDataDto.setEmail(booking.getCustomer().getEmail());
            bookingRequestCustomerDataDto.setPhoneNumber(booking.getCustomer().getPhoneNumber());
        }

        dto.setBookingResponseDto(bookingResponseDto);
        dto.setBookingRequestCustomerDataDto(bookingRequestCustomerDataDto);

        return dto;
    }
}
