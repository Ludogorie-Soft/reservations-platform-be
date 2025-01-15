package ludogorie_soft.reservations_platform_api.mapper;

import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Customer;
import ludogorie_soft.reservations_platform_api.helper.BookingTestHelper;
import ludogorie_soft.reservations_platform_api.helper.CustomerTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class BookingResponseWithCustomerDataMapperTest {

    private Booking booking;
    private Customer customer;

    @BeforeEach
    void setUp() {
        booking = BookingTestHelper.createBooking();
        customer = CustomerTestHelper.createCustomer();
        booking.setCustomer(customer);
    }

    @Test
    void testToBookingWithCustomerDataDto_validData() {
        // GIVEN
        // WHEN
        BookingResponseWithCustomerDataDto dto = BookingResponseWithCustomerDataMapper.toBookingWithCustomerDataDto(booking);

        // THEN
        assertNotNull(dto.getBookingResponseDto());
        assertEquals(booking.getId(), dto.getBookingResponseDto().getId());
        assertEquals(booking.getStartDate().toString(), dto.getBookingResponseDto().getStartDate());
        assertEquals(booking.getEndDate().toString(), dto.getBookingResponseDto().getEndDate());
        assertEquals(booking.getReservationNotes(), dto.getBookingResponseDto().getReservationNotes());
        assertEquals(booking.getAdultCount(), dto.getBookingResponseDto().getAdultCount());
        assertEquals(booking.getChildrenCount(), dto.getBookingResponseDto().getChildrenCount());
        assertEquals(booking.getBabiesCount(), dto.getBookingResponseDto().getBabiesCount());
        assertEquals(booking.isPetContent(), dto.getBookingResponseDto().isPetContent());
        assertEquals(booking.getTotalPrice(), dto.getBookingResponseDto().getTotalPrice());

        assertNotNull(dto.getBookingRequestCustomerDataDto());
        assertEquals(booking.getId(), dto.getBookingRequestCustomerDataDto().getBookingId());
        assertEquals(booking.getCustomer().getFirstName(), dto.getBookingRequestCustomerDataDto().getFirstName());
        assertEquals(booking.getCustomer().getLastName(), dto.getBookingRequestCustomerDataDto().getLastName());
        assertEquals(booking.getCustomer().getEmail(), dto.getBookingRequestCustomerDataDto().getEmail());
        assertEquals(booking.getCustomer().getPhoneNumber(), dto.getBookingRequestCustomerDataDto().getPhoneNumber());
    }

}
