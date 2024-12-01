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
    }

    @Test
    void testToBookingWithCustomerDataDto_validData() {
        // GIVEN
        // WHEN
        BookingResponseWithCustomerDataDto dto = BookingResponseWithCustomerDataMapper.toBookingWithCustomerDataDto(booking, customer);

        // THEN
        assertNotNull(dto.getBookingResponseDto());
        assertEquals(booking.getId(), dto.getBookingResponseDto().getId());
        assertEquals(booking.getStartDate().toString(), dto.getBookingResponseDto().getStartDate());
        assertEquals(booking.getEndDate().toString(), dto.getBookingResponseDto().getEndDate());
        assertEquals(booking.getDescription(), dto.getBookingResponseDto().getDescription());
        assertEquals(booking.getAdultCount(), dto.getBookingResponseDto().getAdultCount());
        assertEquals(booking.getChildrenCount(), dto.getBookingResponseDto().getChildrenCount());
        assertEquals(booking.getBabiesCount(), dto.getBookingResponseDto().getBabiesCount());
        assertEquals(booking.isPetContent(), dto.getBookingResponseDto().isPetContent());
        assertEquals(booking.getTotalPrice(), dto.getBookingResponseDto().getTotalPrice());

        assertNotNull(dto.getBookingRequestCustomerDataDto());
        assertEquals(booking.getId(), dto.getBookingRequestCustomerDataDto().getBookingId());
        assertEquals(customer.getFirstName(), dto.getBookingRequestCustomerDataDto().getFirstName());
        assertEquals(customer.getLastName(), dto.getBookingRequestCustomerDataDto().getLastName());
        assertEquals(customer.getEmail(), dto.getBookingRequestCustomerDataDto().getEmail());
        assertEquals(customer.getPhoneNumber(), dto.getBookingRequestCustomerDataDto().getPhoneNumber());
    }

}
