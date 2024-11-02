package ludogorie_soft.reservations_platform_api.helper;

import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
import ludogorie_soft.reservations_platform_api.dto.BookingResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Property;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

public class BookingTestHelper {

    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final int TEST_ADULT_COUNT = 2;
    private static final int TEST_CHILDREN_COUNT = 1;
    private static final int TEST_BABIES_COUNT = 0;

    public static BookingRequestDto createBookingRequest() {

        LocalDate startLocalDate = LocalDate.now().plusDays(2);
        LocalDate endLocalDate = startLocalDate.plusDays(7);

        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        BookingRequestDto request = new BookingRequestDto();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setDescription(TEST_DESCRIPTION);
        request.setAdultCount(TEST_ADULT_COUNT);
        request.setChildrenCount(TEST_CHILDREN_COUNT);
        request.setBabiesCount(TEST_BABIES_COUNT);
        request.setPetContent(false);
        return request;
    }

    public static Booking createBooking() {
        Booking booking = new Booking();
        Property property = PropertyTestHelper.createDefaultProperty();

        LocalDate startLocalDate = LocalDate.now().plusDays(2);
        LocalDate endLocalDate = startLocalDate.plusDays(7);

        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        booking.setId(UUID.randomUUID());
        booking.setProperty(property);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setDescription(TEST_DESCRIPTION);
        booking.setAdultCount(TEST_ADULT_COUNT);
        booking.setChildrenCount(TEST_CHILDREN_COUNT);
        booking.setBabiesCount(TEST_BABIES_COUNT);
        booking.setTotalPrice(BigDecimal.valueOf(property.getPrice()));
        booking.setPetContent(false);
        return booking;
    }

    public static BookingResponseDto createBookingResponse() {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(UUID.randomUUID());

        LocalDate startLocalDate = LocalDate.now().plusDays(2);
        LocalDate endLocalDate = startLocalDate.plusDays(7);

        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        bookingResponseDto.setStartDate(startDate.toString());
        bookingResponseDto.setEndDate(endDate.toString());
        bookingResponseDto.setDescription(TEST_DESCRIPTION);
        bookingResponseDto.setAdultCount(TEST_ADULT_COUNT);
        bookingResponseDto.setChildrenCount(TEST_CHILDREN_COUNT);
        bookingResponseDto.setBabiesCount(TEST_BABIES_COUNT);
        bookingResponseDto.setPetContent(false);
        return bookingResponseDto;
    }
}
