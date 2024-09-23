package ludogorie_soft.reservations_platform_api.service;

import net.fortuna.ical4j.data.ParserException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface CalendarSyncService {

    String syncCalendar(Long propertyId) throws IOException, ParserException, URISyntaxException;
}
