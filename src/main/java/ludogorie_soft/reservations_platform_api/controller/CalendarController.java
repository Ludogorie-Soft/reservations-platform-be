package ludogorie_soft.reservations_platform_api.controller;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.ReservationResponseDto;
import ludogorie_soft.reservations_platform_api.dto.SyncDto;
import ludogorie_soft.reservations_platform_api.service.CalendarSyncService;
import ludogorie_soft.reservations_platform_api.service.impl.IcsGeneratorServiceImpl;
import net.fortuna.ical4j.data.ParserException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final IcsGeneratorServiceImpl icsGeneratorService;
    private final CalendarSyncService calendarSyncService;

    @GetMapping("/export")
    ResponseEntity<Resource> exportCalendar() {
        String file = icsGeneratorService.createCalendarFileForAllReservations();

        if (file == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        FileSystemResource fileSystemResource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + new File(file).getName())
                .body(fileSystemResource);
    }

    @GetMapping("/sync")
    ResponseEntity<ReservationResponseDto> syncCalendar(@RequestBody SyncDto syncDto) throws ParserException, IOException, URISyntaxException {
        ReservationResponseDto response = calendarSyncService.syncCalendar(syncDto.getUrl());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
