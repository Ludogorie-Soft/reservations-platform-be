package ludogorie_soft.reservations_platform_api.service.impl;

import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.helper.CalendarTestHelper;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import net.fortuna.ical4j.data.ParserException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {

    private static final int ONE_DAY_IN_MILLISECONDS = 86400000;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private CalendarServiceImpl calendarService;

    private UUID propertyId;
    private String testFilePath;
    private String testAirBnbFilePath;

    @BeforeEach
    void setUp() throws IOException {
        propertyId = UUID.randomUUID();
        ReflectionTestUtils.setField(calendarService, "icsMyCalDirectory", "my-calendar");
        ReflectionTestUtils.setField(calendarService, "icsAirBnbDirectory", "air-bnb-calendar");
        testFilePath = "my-calendar.ics";
        testAirBnbFilePath = "airBnbCalendar-1.ics";

        CalendarTestHelper.createTestIcsFile(testFilePath);
        CalendarTestHelper.createTestIcsFile(testAirBnbFilePath);
    }

    @AfterEach
    void cleanUp() {
        File testFile = new File(testFilePath);
        if (testFile.exists()) {
            testFile.delete();
        }
        File testAirBnbFile = new File(testAirBnbFilePath);
        if (testAirBnbFile.exists()) {
            testAirBnbFile.delete();
        }
        File icsMyCalDirectory = new File("my-calendar");
        if (icsMyCalDirectory.exists()) {
            CalendarTestHelper.deleteDirectory(icsMyCalDirectory);
        }
        File icsAirBnbDirectory = new File("air-bnb-calendar");
        if (icsAirBnbDirectory.exists()) {
            CalendarTestHelper.deleteDirectory(icsAirBnbDirectory);
        }
    }

    @Test
    void testGetMyCalendarReturnsFileNameSuccessfully() throws IOException {
        //GIVEN
        Property property = new Property();
        property.setId(propertyId);

        Booking booking = new Booking();
        booking.setStartDate(new Date());
        booking.setEndDate(new Date());
        booking.setReservationNotes("Test Booking");

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(bookingRepository.findByPropertyId(propertyId)).thenReturn(Collections.singletonList(booking));

        //WHEN
        String result = calendarService.getMyCalendar(propertyId);

        //THEN
        assertEquals(propertyId + ".ics", result);
        verify(propertyRepository, times(1)).save(property);
    }

    @Test
    void testGetMyCalendarShouldThrowWhenPropertyNotFound() {
        //WHEN
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        //THEN
        assertThrows(IllegalArgumentException.class, () -> calendarService.getMyCalendar(propertyId));
    }

    @Test
    void testSyncAirBnbCalendarSuccessfully() throws IOException, ParserException {
        //GIVEN
        Property property = new Property();
        property.setId(propertyId);
        property.setAirBnbICalUrl("https://bg.airbnb.com/calendar/ical/1247739757393025285.ics?s=743aa2e810329f14445dd5f53a6279f8");

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        //WHEN
        calendarService.syncAirBnbCalendar(propertyId);

        //THEN
        verify(propertyRepository, times(1)).findById(propertyId);
    }

    @Test
    void testSyncForAvailableDatesReturnsTrueWhenFileExists() throws ParserException, IOException {
        //GIVEN
        String filePath = "test.ics";
        Date startDateRequest = new Date();
        Date endDateRequest = new Date();

        //WHEN
        boolean result = calendarService.syncForAvailableDates(filePath, startDateRequest, endDateRequest);

        //THEN
        assertTrue(result);
    }

    @Test
    void testSyncForAvailableDatesReturnsTrueWhenNoEventsOverlap() throws ParserException, IOException {
        //GIVEN

        Date startDateRequest = new Date(System.currentTimeMillis() + 86400000 * 10);
        Date endDateRequest = new Date(System.currentTimeMillis() + 86400000 * 15);

        File file = new File(testFilePath);
        assertTrue(file.exists());

        //WHEN
        boolean result = calendarService.syncForAvailableDates(testFilePath, startDateRequest, endDateRequest);

        //THEN
        assertTrue(result);
    }

    @Test
    void testSyncForAvailableDatesReturnFalse() throws ParserException, IOException {
        //GIVEN
        Date startDateRequest = new Date();
        Date endDateRequest = new Date();

        //WHEN
        boolean result = calendarService.syncForAvailableDates(testFilePath, startDateRequest, endDateRequest);

        //THEN
        assertFalse(result);
    }

    @Test
    void testGetIcsFileSuccessfully() throws IOException {
        //GIVEN
        Property property = new Property();
        property.setId(propertyId);
        Booking booking = new Booking();
        booking.setStartDate(new Date());
        booking.setEndDate(new Date());
        booking.setReservationNotes("Test Booking");

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(bookingRepository.findByPropertyId(propertyId)).thenReturn(Collections.singletonList(booking));

        //WHEN
        ResponseEntity<FileSystemResource> response = calendarService.getIcsFile(propertyId);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetAirBnbIcsSuccessfully() throws IOException {
        //GIVEN
        Property property = new Property();
        property.setId(propertyId);
        property.setAirBnbICalUrl("https://bg.airbnb.com/calendar/ical/1247739757393025285.ics?s=743aa2e810329f14445dd5f53a6279f8");

        CalendarTestHelper.createTestIcsFile("air-bnb-calendar" + File.separator + "airBnbCalendar-" + propertyId + ".ics");

        //WHEN
        ResponseEntity<FileSystemResource> response = calendarService.getAirBnbIcsFile(propertyId);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}