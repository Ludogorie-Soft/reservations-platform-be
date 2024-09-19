package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.ReservationResponseDto;
import net.fortuna.ical4j.data.ParserException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface CalendarSyncService {

    ReservationResponseDto syncCalendar(String airBnbUrl) throws IOException, ParserException, URISyntaxException;
}
