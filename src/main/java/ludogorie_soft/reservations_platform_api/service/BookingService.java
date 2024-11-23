package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import net.fortuna.ical4j.data.ParserException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) throws IOException, URISyntaxException, ParserException;

    BookingResponseDto getBooking(UUID id);

    List<BookingResponseDto> getAllBookings();

    List<BookingResponseDto> getAllBookingsOfProperty(UUID id);

    BookingResponseDto editBooking(UUID id, BookingRequestDto bookingRequestDto) throws ParserException, IOException;

    BookingResponseDto deleteBooking(UUID id);
}

