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
import ludogorie_soft.reservations_platform_api.service.CalendarSyncService;
import ludogorie_soft.reservations_platform_api.service.PropertyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final PropertyService propertyService;
    private final CalendarSyncService calendarSyncService;

    @Value("${booking.ics.airBnb.directory}")
    private String icsAirBnbDirectory;

    @Value("${booking.ics.myCal.directory}")
    private String icsMyCal;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) throws URISyntaxException, IOException {

        User user = userRepository
                .findByUsernameOrEmail(bookingRequestDto.getEmail(), bookingRequestDto.getEmail())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "User with this username or email not found!"));

        Property property = propertyService.findById(bookingRequestDto.getPropertyId());

        if (bookingRequestDto.getEndDate().before(bookingRequestDto.getStartDate())) {
            throw new IllegalArgumentException("Start date must be before the end date!");
        }

        boolean checkMyCal = calendarSyncService.syncForAvailableDates(
                icsMyCal + File.separator + property.getId() + ".ics",
                bookingRequestDto.getStartDate(),
                bookingRequestDto.getEndDate());

        boolean checkAirBnbCal = calendarSyncService.syncForAvailableDates(
                icsAirBnbDirectory + File.separator + "airBnbCalendar-" + property.getId() + ".ics",
                bookingRequestDto.getStartDate(),
                bookingRequestDto.getEndDate());

        if (checkMyCal && checkAirBnbCal) {
            Booking booking = new Booking();
            booking.setProperty(property);
            booking.setUser(user);
            booking.setStartDate(bookingRequestDto.getStartDate());
            booking.setEndDate(bookingRequestDto.getEndDate());
            booking.setDescription(bookingRequestDto.getDescription());

            Booking createdBooking = bookingRepository.save(booking);

            calendarSyncService.createMyCalendar(property.getId(), bookingRequestDto);

            return modelMapper.map(createdBooking, BookingResponseDto.class);
        } else {
            throw new IllegalArgumentException("These dates are not available!");
        }
    }
}
