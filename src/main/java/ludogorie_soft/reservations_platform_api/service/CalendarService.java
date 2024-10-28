package ludogorie_soft.reservations_platform_api.service;

import net.fortuna.ical4j.data.ParserException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

public interface CalendarService {
    String getMyCalendar(UUID propertyId) throws IOException;

    void syncAirBnbCalendar(UUID propertyId) throws URISyntaxException, IOException, ParserException, ParseException;

    boolean syncForAvailableDates(String filePath, Date startDateRequest, Date endDateRequest) throws IOException, ParserException;

    ResponseEntity<FileSystemResource> getIcsFile(UUID propertyId) throws IOException;

    ResponseEntity<FileSystemResource> getAirBnbIcsFile(UUID propertyId);
}
