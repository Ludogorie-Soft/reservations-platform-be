package ludogorie_soft.reservations_platform_api.controller;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.ReservationResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Reservation;
import ludogorie_soft.reservations_platform_api.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    ResponseEntity<ReservationResponseDto> createReservation(@RequestBody Reservation reservation) throws FileNotFoundException, URISyntaxException {
        ReservationResponseDto response = reservationService.createReservation(reservation);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
