package ludogorie_soft.reservations_platform_api.mapper;

import lombok.NoArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestCustomerDataDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@NoArgsConstructor
public class BookingResponseWithCustomerDataMapper {

    public static BookingResponseWithCustomerDataDto toBookingWithCustomerDataDto(Booking booking, Customer customer) {
        BookingResponseDto bookingResponseDto = createBookingResponseDto(booking);
        BookingRequestCustomerDataDto bookingRequestCustomerDataDto = createBookingRequestCustomerDataDto(booking.getId(), customer);

        return createDto(bookingResponseDto, bookingRequestCustomerDataDto);
    }

    public static BookingResponseWithCustomerDataDto toBookingWithCustomerDataDto(Booking booking) {
        BookingResponseDto bookingResponseDto = createBookingResponseDto(booking);
        BookingRequestCustomerDataDto bookingRequestCustomerDataDto = createBookingRequestCustomerDataDto(booking.getId(), booking.getCustomer());

        return createDto(bookingResponseDto, bookingRequestCustomerDataDto);
    }

    private static BookingResponseDto createBookingResponseDto(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStartDate(booking.getStartDate().toString());
        bookingResponseDto.setEndDate(booking.getEndDate().toString());
        bookingResponseDto.setDescription(booking.getDescription());
        bookingResponseDto.setAdultCount(booking.getAdultCount());
        bookingResponseDto.setChildrenCount(booking.getChildrenCount());
        bookingResponseDto.setBabiesCount(booking.getBabiesCount());
        bookingResponseDto.setPetContent(booking.isPetContent());
        bookingResponseDto.setTotalPrice(booking.getTotalPrice());
        return bookingResponseDto;
    }

    private static BookingRequestCustomerDataDto createBookingRequestCustomerDataDto(UUID bookingId, Customer customer) {
        BookingRequestCustomerDataDto bookingRequestCustomerDataDto = new BookingRequestCustomerDataDto();
        bookingRequestCustomerDataDto.setBookingId(bookingId);
        if (customer != null) {
            bookingRequestCustomerDataDto.setFirstName(customer.getFirstName());
            bookingRequestCustomerDataDto.setLastName(customer.getLastName());
            bookingRequestCustomerDataDto.setEmail(customer.getEmail());
            bookingRequestCustomerDataDto.setPhoneNumber(customer.getPhoneNumber());
        }
        return bookingRequestCustomerDataDto;
    }

    private static BookingResponseWithCustomerDataDto createDto(BookingResponseDto bookingResponseDto,
                                                                BookingRequestCustomerDataDto bookingRequestCustomerDataDto) {

        BookingResponseWithCustomerDataDto dto = new BookingResponseWithCustomerDataDto();
        dto.setBookingResponseDto(bookingResponseDto);
        dto.setBookingRequestCustomerDataDto(bookingRequestCustomerDataDto);
        return dto;
    }
}
