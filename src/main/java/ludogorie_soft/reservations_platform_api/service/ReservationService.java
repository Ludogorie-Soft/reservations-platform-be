package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.ReservationResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Reservation;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;

public interface ReservationService {

    ReservationResponseDto createReservation(Reservation reservation) throws FileNotFoundException, URISyntaxException;
    List<ReservationResponseDto> getAllReservations();
    List<Reservation> findAllReservations();
    boolean existsByUid(String uid);
    Reservation findByUid(String uid);
    void updateReservation(Reservation reservation);
}
