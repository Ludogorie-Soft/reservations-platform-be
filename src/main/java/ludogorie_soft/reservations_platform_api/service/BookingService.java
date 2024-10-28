package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import net.fortuna.ical4j.data.ParserException;
import java.io.IOException;
import java.net.URISyntaxException;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) throws IOException, URISyntaxException, ParserException;
}
