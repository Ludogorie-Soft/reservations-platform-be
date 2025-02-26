package ludogorie_soft.reservations_platform_api.controller;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.service.CalendarService;
import net.fortuna.ical4j.data.ParserException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.UUID;

@RestController
@RequestMapping("/calendar/ical")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/{propertyId}")
    public ResponseEntity<FileSystemResource> getIcsFile(@PathVariable("propertyId") UUID propertyId) throws IOException {
        return calendarService.getIcsFile(propertyId);
    }

    @GetMapping("/air-bnb/{propertyId}")
    public ResponseEntity<FileSystemResource> getAirBnbIcsFile(@PathVariable("propertyId") UUID propertyId) {
        return calendarService.getAirBnbIcsFile(propertyId);
    }

    @GetMapping("/air-bnb/{propertyId}/sync")
    public ResponseEntity<String> syncAirbnbCalendar(@PathVariable("propertyId") UUID propertyId) throws IOException, ParserException, URISyntaxException, ParseException {
        calendarService.syncAirBnbCalendar(propertyId);
        return ResponseEntity.ok("Calendar sync successfully!");
    }
}
