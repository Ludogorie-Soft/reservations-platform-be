package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.ReservationResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Reservation;
import ludogorie_soft.reservations_platform_api.repository.ReservationRepository;
import ludogorie_soft.reservations_platform_api.service.ReservationService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final IcsGeneratorServiceImpl icsGeneratorService;
    private final ModelMapper modelMapper;

    @Override
    public ReservationResponseDto createReservation(Reservation reservation) throws URISyntaxException {
        Reservation savedReservation = reservationRepository.save(reservation);
        icsGeneratorService.createCalendarEvent(savedReservation);
        return modelMapper.map(savedReservation, ReservationResponseDto.class);
    }

    @Override
    public List<ReservationResponseDto> getAllReservations() {
        return findAllReservations().stream()
                .map(reservation -> modelMapper.map(reservation, ReservationResponseDto.class))
                .toList();
    }

    @Override
    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public boolean existsByUid(String uid) {
        return reservationRepository.existsByUid(uid);
    }

    @Override
    public Reservation findByUid(String uid) {
        return reservationRepository.findByUid(uid);
    }

    @Override
    public void updateReservation(Reservation reservation) {
       reservationRepository.save(reservation);
       //TODO: update .ics file for the current reservation
    }
}
