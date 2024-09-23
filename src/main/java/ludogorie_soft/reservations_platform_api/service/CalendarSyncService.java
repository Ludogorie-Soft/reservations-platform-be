package ludogorie_soft.reservations_platform_api.service;

import net.fortuna.ical4j.data.ParserException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

public interface CalendarSyncService {
    void syncAirBnbCalendar(Long propertyId) throws URISyntaxException, IOException, ParserException, ParseException;
}
