package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.service.BookingService;
import ludogorie_soft.reservations_platform_api.service.CalendarService;
import ludogorie_soft.reservations_platform_api.service.PropertyService;
import ludogorie_soft.reservations_platform_api.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final PropertyService propertyService;
    private final CalendarService calendarService;

    @Value("${booking.ics.airBnb.directory}")
    private String icsAirBnbDirectory;

    @Value("${booking.ics.myCal.directory}")
    private String icsMyCal;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {

        Property property = propertyService.findById(bookingRequestDto.getPropertyId());

        if (bookingRequestDto.getEndDate().before(bookingRequestDto.getStartDate())) {
            throw new IllegalArgumentException("Start date must be before the end date!");
        }

        List<Booking> checkedBookings = bookingRepository.findByStartDateAndEndDate(bookingRequestDto.getStartDate(), bookingRequestDto.getEndDate());

        boolean checkAirBnbCal = calendarService.syncForAvailableDates(
                icsAirBnbDirectory + File.separator + "airBnbCalendar-" + property.getId() + ".ics",
                bookingRequestDto.getStartDate(),
                bookingRequestDto.getEndDate());

        if (checkedBookings.isEmpty() && checkAirBnbCal) {
            Booking createdBooking = createBookingModel(bookingRequestDto);
            return modelMapper.map(createdBooking, BookingResponseDto.class);
        } else {
            throw new IllegalArgumentException("These dates are not available!");
        }
    }

    private Booking createBookingModel(BookingRequestDto bookingRequestDto) {
        User user = userService.getUserByEmailOrUsername(bookingRequestDto.getEmail(), bookingRequestDto.getEmail());
        Property property = propertyService.findById(bookingRequestDto.getPropertyId());
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setUser(user);
        booking.setStartDate(bookingRequestDto.getStartDate());
        booking.setEndDate(bookingRequestDto.getEndDate());
        booking.setDescription(bookingRequestDto.getDescription());
        booking.setUid(property.getId() + "-" + UUID.randomUUID());
        return bookingRepository.save(booking);
    }
}
