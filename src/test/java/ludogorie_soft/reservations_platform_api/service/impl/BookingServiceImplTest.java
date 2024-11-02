package ludogorie_soft.reservations_platform_api.service.impl;

import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.exception.BookingNotFoundException;
import ludogorie_soft.reservations_platform_api.exception.InvalidCapacityException;
import ludogorie_soft.reservations_platform_api.exception.InvalidDateRequestException;
import ludogorie_soft.reservations_platform_api.exception.NotAvailableDatesException;
import ludogorie_soft.reservations_platform_api.helper.BookingTestHelper;
import ludogorie_soft.reservations_platform_api.helper.PropertyTestHelper;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.service.CalendarService;
import net.fortuna.ical4j.data.ParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PropertyServiceImpl propertyService;

    @Mock
    private CalendarService calendarService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Property property;
    private BookingRequestDto bookingRequestDto;
    private Booking createdBooking;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setup() {
        property = PropertyTestHelper.createDefaultProperty();
        bookingRequestDto = BookingTestHelper.createBookingRequest();
        createdBooking = BookingTestHelper.createBooking();
        bookingResponseDto = BookingTestHelper.createBookingResponse();
        ReflectionTestUtils.setField(bookingService, "icsAirBnbDirectory", "air-bnb-calendar");
    }

    @Test
    void createBookingWithNoPetsSuccessfully() throws ParserException, IOException {
        // GIVEN
        when(propertyService.findById(property.getId())).thenReturn(property);
        bookingRequestDto.setPropertyId(property.getId());
        when(calendarService.syncForAvailableDates(anyString(), any(), any())).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenReturn(createdBooking);
        bookingResponseDto.setTotalPrice(BigDecimal.valueOf(property.getPrice()));
        when(modelMapper.map(createdBooking, BookingResponseDto.class)).thenReturn(bookingResponseDto);

        // WHEN
        BookingResponseDto response = bookingService.createBooking(bookingRequestDto);

        // THEN
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(property.getPrice()), response.getTotalPrice());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBookingWithPetContent() throws ParserException, IOException {
        // GIVEN
        property.setPetAllowed(true);
        property.setPetPrice(20);
        when(propertyService.findById(property.getId())).thenReturn(property);
        bookingRequestDto.setPropertyId(property.getId());
        bookingRequestDto.setPetContent(true);
        when(calendarService.syncForAvailableDates(anyString(), any(), any())).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenReturn(createdBooking);
        bookingResponseDto.setPetContent(true);
        bookingResponseDto.setTotalPrice(BigDecimal.valueOf(property.getPrice() + property.getPetPrice()));
        when(modelMapper.map(createdBooking, BookingResponseDto.class)).thenReturn(bookingResponseDto);

        // WHEN
        BookingResponseDto response = bookingService.createBooking(bookingRequestDto);

        // THEN
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(property.getPrice() + property.getPetPrice()), response.getTotalPrice());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBookingShouldThrowWhenTheDatesAreInThePast() {
        // GIVEN
        when(propertyService.findById(property.getId())).thenReturn(property);
        bookingRequestDto.setPropertyId(property.getId());
        LocalDate startLocalDate = LocalDate.now().minusDays(7);
        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        bookingRequestDto.setStartDate(startDate);

        // WHEN & THEN
        assertThrows(InvalidDateRequestException.class,
                () -> bookingService.createBooking(bookingRequestDto));
    }

    @Test
    void testCreateBookingShouldThrowWhenStartDateIsAfterEndDate() {
        // GIVEN
        when(propertyService.findById(property.getId())).thenReturn(property);
        bookingRequestDto.setPropertyId(property.getId());

        LocalDate startLocalDate = LocalDate.now().plusDays(2);
        LocalDate endLocalDate = startLocalDate.plusDays(7);

        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        bookingRequestDto.setStartDate(endDate);
        bookingRequestDto.setEndDate(startDate);

        // WHEN & THEN
        assertThrows(InvalidDateRequestException.class,
                () -> bookingService.createBooking(bookingRequestDto));
    }

    @Test
    void testCreateBookingShouldThrowWhenCapacityExceeded() {
        // GIVEN
        when(propertyService.findById(property.getId())).thenReturn(property);
        bookingRequestDto.setPropertyId(property.getId());
        bookingRequestDto.setAdultCount(20);

        // WHEN & THEN
        assertThrows(InvalidCapacityException.class, () -> bookingService.createBooking(bookingRequestDto));
    }

    @Test
    void testCreateBookingShouldThrowWhenTheDatesAreNotAvailable() throws IOException, ParserException {
        // GIVEN
        when(propertyService.findById(any(UUID.class))).thenReturn(property);
        bookingRequestDto.setPropertyId(property.getId());
        when(calendarService.syncForAvailableDates(anyString(), any(), any())).thenReturn(false);

        // WHEN & THEN
        assertThrows(NotAvailableDatesException.class, () -> bookingService.createBooking(bookingRequestDto));
    }

    @Test
    void testGetBookingSuccessfully() {
        // GIVEN
        when(bookingRepository.findById(any(UUID.class))).thenReturn(Optional.of(createdBooking));
        when(modelMapper.map(createdBooking, BookingResponseDto.class)).thenReturn(bookingResponseDto);

        // WHEN
        BookingResponseDto response = bookingService.getBooking(createdBooking.getId());

        // THEN
        assertNotNull(response);
        verify(bookingRepository).findById(createdBooking.getId());
    }

    @Test
    void testGetBookingShouldThrowWhenBookingNotFound() {
        // GIVEN
        when(bookingRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        //WHEN & THEN
        assertThrows(BookingNotFoundException.class, () -> bookingService.getBooking(UUID.randomUUID()));
    }


    @Test
    void testDeleteBookingSuccessfully() {
        // GIVEN
        when(bookingRepository.findById(createdBooking.getId())).thenReturn(Optional.of(createdBooking));

        // WHEN
        bookingService.deleteBooking(createdBooking.getId());

        // THEN
        verify(bookingRepository).delete(createdBooking);
    }

    @Test
    void testDeleteBookingShouldThrowWhenBookingNotFound() {
        // GIVEN
        when(bookingRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(BookingNotFoundException.class, () -> bookingService.deleteBooking(createdBooking.getId()));
    }


    @Test
    void testEditBookingSuccessfully() throws IOException, ParserException {
        // GIVEN
        when(bookingRepository.findById(createdBooking.getId())).thenReturn(Optional.of(createdBooking));
        when(modelMapper.map(createdBooking, BookingResponseDto.class)).thenReturn(bookingResponseDto);

        BookingRequestDto editRequestDto = BookingTestHelper.createBookingRequest();
        editRequestDto.setAdultCount(1);
        editRequestDto.setChildrenCount(1);

        // WHEN
        BookingResponseDto response = bookingService.editBooking(createdBooking.getId(), editRequestDto);

        // THEN
        assertNotNull(response);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testEditBookingShouldThrowWhenBookingNotFound() {
        // GIVEN
        when(bookingRepository.findById(createdBooking.getId())).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(BookingNotFoundException.class, () -> bookingService.editBooking(createdBooking.getId(), bookingRequestDto));
    }

    @Test
    void testEditBookingShouldThrowWhenTeDatesAreInThePast() {
        // GIVEN
        bookingRequestDto.setStartDate(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        bookingRequestDto.setEndDate(new Date());
        when(bookingRepository.findById(createdBooking.getId())).thenReturn(Optional.of(createdBooking));

        // WHEN & THEN
        assertThrows(InvalidDateRequestException.class, () -> bookingService.editBooking(createdBooking.getId(), bookingRequestDto));
    }

    @Test
    void testEditBookingShouldThrowWhenStartDateIsAfterEndDate() {
        // GIVEN
        bookingRequestDto.setStartDate(Date.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        bookingRequestDto.setEndDate(Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        when(bookingRepository.findById(createdBooking.getId())).thenReturn(Optional.of(createdBooking));

        // WHEN & THEN
        assertThrows(InvalidDateRequestException.class, () -> bookingService.editBooking(createdBooking.getId(), bookingRequestDto));
    }

    @Test
    void testEditBookingShouldThrowWhenCapacityExceeded() {
        // GIVEN
        bookingRequestDto.setAdultCount(30);
        when(bookingRepository.findById(createdBooking.getId())).thenReturn(Optional.of(createdBooking));

        // WHEN & THEN
        assertThrows(InvalidCapacityException.class, () -> bookingService.editBooking(createdBooking.getId(), bookingRequestDto));
    }

    @Test
    void testEditBookingShouldThrowWhenTheDatesAreNotAvailable() throws IOException, ParserException {
        // GIVEN
        Booking secondBooking = BookingTestHelper.createBooking();
        secondBooking.setStartDate(Date.from(LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        secondBooking.setEndDate(Date.from(LocalDate.now().plusDays(13).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        when(bookingRepository.findById(createdBooking.getId())).thenReturn(Optional.of(createdBooking));

        BookingRequestDto editRequestDto = BookingTestHelper.createBookingRequest();
        editRequestDto.setStartDate(secondBooking.getStartDate());
        editRequestDto.setEndDate(secondBooking.getEndDate());

        // WHEN & THEN
        assertThrows(NotAvailableDatesException.class, () -> bookingService.editBooking(createdBooking.getId(), editRequestDto));
    }

}