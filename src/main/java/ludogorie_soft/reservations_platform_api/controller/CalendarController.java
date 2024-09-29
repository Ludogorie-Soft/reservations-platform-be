package ludogorie_soft.reservations_platform_api.controller;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.service.CalendarSyncService;
import ludogorie_soft.reservations_platform_api.service.PropertyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/calendar/ical")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarSyncService calendarSyncService;
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
        try {

            File file = new File(icsMyCalDirectory + File.separator + filename);

            if (file.exists()) {
                FileSystemResource resource = new FileSystemResource(file);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

                return ResponseEntity.ok()
                        .headers(headers)
//                        .contentLength(file.length())
//                        .contentType(MediaType.parseMediaType("text/calendar"))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
