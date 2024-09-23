package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.exception.APIException;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.UserRepository;
import ludogorie_soft.reservations_platform_api.service.BookingService;
import ludogorie_soft.reservations_platform_api.service.PropertyService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final IcsGeneratorServiceImpl icsGeneratorService;
    private final ModelMapper modelMapper;
    private final PropertyService propertyService;

    @Override
    public BookingResponseDto createReservation(BookingRequestDto bookingRequestDto) throws URISyntaxException {

        User user = userRepository
                .findByUsernameOrEmail(bookingRequestDto.getEmail(), bookingRequestDto.getEmail())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "User with this username or email not found!"));

        Property property = propertyService.findById(bookingRequestDto.getPropertyId());

        if (bookingRequestDto.getEndDate().before(bookingRequestDto.getStartDate())) {
            throw new IllegalArgumentException("Start date must be before end date!");
        }

        List<Booking> bookings = bookingRepository
                .findByStartDateAndEndDate(bookingRequestDto.getStartDate(), bookingRequestDto.getEndDate());

        if (bookings.isEmpty()) {
            Booking booking = new Booking();
            booking.setProperty(property);
            booking.setEmail(user.getEmail());
            booking.setStartDate(bookingRequestDto.getStartDate());
            booking.setEndDate(bookingRequestDto.getEndDate());
            booking.setDescription(bookingRequestDto.getDescription());

            Booking createdBooking = bookingRepository.save(booking);
            icsGeneratorService.createCalendarEvent(createdBooking);
            return modelMapper.map(createdBooking, BookingResponseDto.class);
        }

        return null;
    }

    @Override
    public List<BookingResponseDto> getAllReservations() {
        return findAllReservations().stream()
                .map(reservation -> modelMapper.map(reservation, BookingResponseDto.class))
                .toList();
    }

    @Override
    public List<Booking> findAllReservations() {
        return bookingRepository.findAll();
    }

    @Override
    public boolean existsByUid(String uid) {
        return bookingRepository.existsByUid(uid);
    }

    @Override
    public Booking findByUid(String uid) {
        return bookingRepository.findByUid(uid);
    }

    @Override
    public void updateReservation(Booking booking) {
        bookingRepository.save(booking);
        //TODO: update .ics file for the current reservation
    }

    @Override
    public Booking findById(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Reservation not found!")
        );
    }
}
