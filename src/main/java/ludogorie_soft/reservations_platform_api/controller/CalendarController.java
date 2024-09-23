package ludogorie_soft.reservations_platform_api.controller;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.service.CalendarSyncService;
import ludogorie_soft.reservations_platform_api.service.impl.IcsGeneratorServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private final IcsGeneratorServiceImpl icsGeneratorService;
    private final CalendarSyncService calendarSyncService;

    @Value("${booking.ics.directory}")
    private String icsDirectory;

    // Get URL of .ics for reservation with Uid:reservationUid
    @GetMapping("/booking/{reservationUid}")
    public ResponseEntity<String> getIcsFileUrl(@PathVariable("reservationUid") String reservationUid) {
        String fileUrl = icsGeneratorService.getUrlForBooking(reservationUid);
        return ResponseEntity.ok(fileUrl);
    }

    // Download .ics file
    @GetMapping("/{filename}")
    public ResponseEntity<FileSystemResource> getIcsFile(@PathVariable("filename") String filename) {
        try {

            File file = new File(icsDirectory + File.separator + filename);

            if (file.exists()) {
                FileSystemResource resource = new FileSystemResource(file);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
