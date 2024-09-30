package ludogorie_soft.reservations_platform_api.controller;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.service.CalendarService;
import ludogorie_soft.reservations_platform_api.service.PropertyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calendar/ical")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;
    private final PropertyService propertyService;

    @Value("${booking.ics.myCal.directory}")
    private String icsMyCalDirectory;

    //  Get URL of .ics for property with id:propertyId
    @GetMapping("/bookings/{propertyId}")
    public ResponseEntity<String> getIcsFileUrl(@PathVariable("propertyId") Long propertyId) {
        String fileUrl = propertyService.getPropertySyncUrl(propertyId);
        return ResponseEntity.ok(fileUrl);
    }

    // Download .ics file
    @GetMapping("/{filename}")
    public ResponseEntity<FileSystemResource> getIcsFile(@PathVariable("filename") String filename) {
        return calendarService.getIcsFile(filename);
    }
}
