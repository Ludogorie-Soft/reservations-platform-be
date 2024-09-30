package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import net.fortuna.ical4j.data.ParserException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;

public interface CalendarService {
    String createMyCalendar(Long propertyId, BookingRequestDto requestDto) throws IOException, URISyntaxException;

    void syncAirBnbCalendar(Long propertyId) throws URISyntaxException, IOException, ParserException, ParseException;

    boolean syncForAvailableDates(String filePath, Date startDateRequest, Date endDateRequest);

    ResponseEntity<FileSystemResource> getIcsFile(String filename);
}
