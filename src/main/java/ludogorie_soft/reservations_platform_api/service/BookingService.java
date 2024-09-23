package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;

public interface BookingService {

    BookingResponseDto createReservation(BookingRequestDto bookingRequestDto) throws FileNotFoundException, URISyntaxException;
    List<BookingResponseDto> getAllReservations();
    List<Booking> findAllReservations();
    boolean existsByUid(String uid);
    Booking findByUid(String uid);
    void updateReservation(Booking booking);
    Booking findById(Long id);
}
