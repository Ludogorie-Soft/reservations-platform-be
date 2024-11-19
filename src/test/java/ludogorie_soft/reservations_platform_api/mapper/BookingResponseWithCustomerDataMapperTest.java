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

@ExtendWith(MockitoExtension.class)
public class BookingResponseWithCustomerDataMapperTest {

    private Booking booking;
    private Customer customer;

    @BeforeEach
    public void setUp() {
        booking = BookingTestHelper.createBooking();
        customer = CustomerTestHelper.createCustomer();
    }

    @Test
    public void testToBookingWithCustomerDataDto_validData() {
        // GIVEN
        // WHEN
        BookingResponseWithCustomerDataDto dto = BookingResponseWithCustomerDataMapper.toBookingWithCustomerDataDto(booking, customer);

        // THEN
        assertEquals(booking.getId(), dto.getBookingId());
        assertEquals(booking.getStartDate().toString(), dto.getStartDate());
        assertEquals(booking.getEndDate().toString(), dto.getEndDate());
        assertEquals(booking.getDescription(), dto.getDescription());
        assertEquals(booking.getAdultCount(), dto.getAdultCount());
        assertEquals(booking.getChildrenCount(), dto.getChildrenCount());
        assertEquals(booking.getBabiesCount(), dto.getBabiesCount());
        assertEquals(booking.isPetContent(), dto.isPetContent());
        assertEquals(booking.getTotalPrice(), dto.getTotalPrice());
        assertEquals(customer.getFirstName(), dto.getFirstName());
        assertEquals(customer.getLastName(), dto.getLastName());
        assertEquals(customer.getEmail(), dto.getEmail());
        assertEquals(customer.getPhoneNumber(), dto.getPhoneNumber());
    }

}
