package ludogorie_soft.reservations_platform_api.mapper;

import lombok.NoArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestCustomerDataDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
public class BookingResponseWithCustomerDataMapper {

    public static BookingResponseWithCustomerDataDto toBookingWithCustomerDataDto(Booking booking) {
        BookingResponseWithCustomerDataDto dto = new BookingResponseWithCustomerDataDto();
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        BookingRequestCustomerDataDto bookingRequestCustomerDataDto = new BookingRequestCustomerDataDto();

        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setPropertyId(booking.getProperty().getId());
        bookingResponseDto.setStartDate(booking.getStartDate().toString());
        bookingResponseDto.setEndDate(booking.getEndDate().toString());
        bookingResponseDto.setReservationNotes(booking.getReservationNotes());
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
            bookingRequestCustomerDataDto.setReservationNotes(booking.getReservationNotes());
        }

        dto.setBookingResponseDto(bookingResponseDto);
        dto.setBookingRequestCustomerDataDto(bookingRequestCustomerDataDto);

        return dto;
    }
}
