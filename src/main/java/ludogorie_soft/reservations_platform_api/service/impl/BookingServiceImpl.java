package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.exception.BookingNotFoundException;
import ludogorie_soft.reservations_platform_api.exception.InvalidCapacityException;
import ludogorie_soft.reservations_platform_api.exception.InvalidDateRequestExceptionException;
import ludogorie_soft.reservations_platform_api.exception.NotAvailableDatesException;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.service.BookingService;
import ludogorie_soft.reservations_platform_api.service.CalendarService;
import ludogorie_soft.reservations_platform_api.service.PropertyService;
import net.fortuna.ical4j.data.ParserException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final PropertyService propertyService;
    private final CalendarService calendarService;

    @Value("${booking.ics.airBnb.directory}")
    private String icsAirBnbDirectory;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) throws ParserException, IOException {

        Property property = propertyService.findById(bookingRequestDto.getPropertyId());

        checkDatesAreInPast(bookingRequestDto);
        checkStartDateIsBeforeEndDate(bookingRequestDto);
        int peopleInRequest = (bookingRequestDto.getAdultCount() + bookingRequestDto.getChildrenCount());
        checkCapacity(peopleInRequest, property);

        if (checkCalendarsForAvailableDates(property, bookingRequestDto)) {
            Booking createdBooking = createBookingModel(bookingRequestDto, property);
            return modelMapper.map(createdBooking, BookingResponseDto.class);
        } else {
            throw new NotAvailableDatesException("These dates are not available!");
        }
    }

    @Override
    public BookingResponseDto editBooking(UUID id, BookingRequestDto request) throws ParserException, IOException {
        Booking booking = getBookingById(id);

        checkDatesAreInPast(request);
        checkStartDateIsBeforeEndDate(request);

        int peopleInRequest = (request.getAdultCount() + request.getChildrenCount());
        checkCapacity(peopleInRequest, booking.getProperty());

        boolean areDatesAvailable = true;
        if (!checkStartDateAndEndDateAreChangedByEdit(booking, request)) {
            booking.setStartDate(null);
            booking.setEndDate(null);
            bookingRepository.save(booking);
            areDatesAvailable = checkCalendarsForAvailableDates(booking.getProperty(), request);
        }

        if (areDatesAvailable) {
            setBookingFields(request, booking.getProperty(), booking);
            bookingRepository.save(booking);
            return modelMapper.map(booking, BookingResponseDto.class);
        } else {
            throw new NotAvailableDatesException("These dates are not available!");
        }
    }

    @Override
    public BookingResponseDto getBooking(UUID id) {
        Booking booking = getBookingById(id);
        return modelMapper.map(booking, BookingResponseDto.class);
    }

    @Override
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(booking -> modelMapper.map(booking, BookingResponseDto.class))
                .toList();
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfProperty(UUID id) {
        return bookingRepository.findByPropertyId(id).stream()
                .map(booking -> modelMapper.map(booking, BookingResponseDto.class))
                .toList();
    }


    @Override
    public BookingResponseDto deleteBooking(UUID id) {
        Booking booking = getBookingById(id);
        if (booking != null) {
            bookingRepository.delete(booking);
        }
        return modelMapper.map(booking, BookingResponseDto.class);
    }

    private Booking createBookingModel(BookingRequestDto bookingRequestDto, Property property) {
        Booking booking = new Booking();
        setBookingFields(bookingRequestDto, property, booking);
        return bookingRepository.save(booking);
    }

    private void setBookingFields(BookingRequestDto bookingRequestDto, Property property, Booking booking) {
        booking.setProperty(property);
        booking.setStartDate(bookingRequestDto.getStartDate());
        booking.setEndDate(bookingRequestDto.getEndDate());
        booking.setDescription(bookingRequestDto.getDescription());
        booking.setAdultCount(bookingRequestDto.getAdultCount());
        booking.setChildrenCount(bookingRequestDto.getChildrenCount());
        booking.setBabiesCount(bookingRequestDto.getBabiesCount());
        int totalPrice = property.getPrice();
        if (bookingRequestDto.isPetContent()) {
            totalPrice = property.getPrice() + property.getPetPrice();
        }
        booking.setTotalPrice(BigDecimal.valueOf(totalPrice));
    }

    private boolean checkCalendarsForAvailableDates(Property property, BookingRequestDto bookingRequestDto) throws ParserException, IOException {
        List<Booking> checkedBookings = bookingRepository.findBookingsByPropertyIdAndDateRange(property.getId(), bookingRequestDto.getStartDate(), bookingRequestDto.getEndDate());

        boolean checkAirBnbCal = calendarService.syncForAvailableDates(
                icsAirBnbDirectory + File.separator + "airBnbCalendar-" + property.getId() + ".ics",
                bookingRequestDto.getStartDate(),
                bookingRequestDto.getEndDate());
        return checkedBookings.isEmpty() && checkAirBnbCal;
    }

    private boolean isDateInPast(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.isBefore(LocalDate.now());
    }

    private Booking getBookingById(UUID id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new BookingNotFoundException("Booking with id + " + id + " not found!")
        );
    }

    private static void checkStartDateIsBeforeEndDate(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getEndDate().before(bookingRequestDto.getStartDate())) {
            throw new InvalidDateRequestExceptionException("Start date must be before the end date!");
        }
    }

    private void checkDatesAreInPast(BookingRequestDto bookingRequestDto) {
        if (isDateInPast(bookingRequestDto.getStartDate()) || isDateInPast(bookingRequestDto.getEndDate())) {
            throw new InvalidDateRequestExceptionException("Booking dates cannot be in the past!");
        }
    }

    private static void checkCapacity(int peopleInRequest, Property property) {
        if (peopleInRequest > property.getCapacity()) {
            throw new InvalidCapacityException("The capacity is " + property.getCapacity() + "persons, your request is " + peopleInRequest + " persons.");
        }
    }

    private static boolean checkStartDateAndEndDateAreChangedByEdit(Booking booking, BookingRequestDto request) {
        return request.getStartDate().equals(booking.getStartDate()) && request.getEndDate().equals(booking.getEndDate());
    }
}
