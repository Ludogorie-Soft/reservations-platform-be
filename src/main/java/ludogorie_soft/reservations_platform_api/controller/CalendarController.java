package ludogorie_soft.reservations_platform_api.controller;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.service.CalendarService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/calendar/ical")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/{propertyId}")
    public ResponseEntity<FileSystemResource> getIcsFile(@PathVariable("propertyId") Long propertyId) throws IOException {
        return calendarService.getIcsFile(propertyId);
    }

    @GetMapping("/air-bnb/{propertyId}")
    public ResponseEntity<FileSystemResource> getAirBnbIcsFile(@PathVariable("propertyId") Long propertyId) {
        return calendarService.getAirBnbIcsFile(propertyId);
    }
}
