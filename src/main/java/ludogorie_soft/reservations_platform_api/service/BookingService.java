package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.BookingRequestCustomerDataDto;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import net.fortuna.ical4j.data.ParserException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) throws IOException, URISyntaxException, ParserException;

    BookingResponseDto getBooking(UUID id);

    List<BookingResponseWithCustomerDataDto> getAllBookings();

    List<BookingResponseDto> getAllBookingsOfProperty(UUID id);

    BookingResponseDto editBooking(UUID id, BookingRequestDto bookingRequestDto) throws ParserException, IOException;

    BookingResponseDto deleteBooking(UUID id);

    BookingResponseWithCustomerDataDto addCustomerDataToBooking(BookingRequestCustomerDataDto bookingRequestCustomerDataDto);
}

