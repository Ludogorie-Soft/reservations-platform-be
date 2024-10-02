package ludogorie_soft.reservations_platform_api.service.impl;

import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalendarServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private CalendarServiceImpl calendarService;

    private Long propertyId;
    private String testFilePath;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        propertyId = 1L;
        ReflectionTestUtils.setField(calendarService, "icsMyCalDirectory", "my-calendar");
        ReflectionTestUtils.setField(calendarService, "icsAirBnbDirectory", "air-bnb-calendar");
        testFilePath = "my-calendar.ics";
        createTestIcsFile(testFilePath);
    }

    @AfterEach
    void cleanUp() {
        File testFile = new File(testFilePath);
        if (testFile.exists()) {
            testFile.delete();
        }
        File icsMyCalDirectory = new File("my-calendar");
        if (icsMyCalDirectory.exists()) {
            deleteDirectory(icsMyCalDirectory);
        }
        File icsAirBnbDirectory = new File("air-bnb-calendar");
        if (icsAirBnbDirectory.exists()) {
            deleteDirectory(icsAirBnbDirectory);
        }
    }

    @Test
    void testGetMyCalendarReturnsFileNameSuccessfully() throws IOException {
        Property property = new Property();
        property.setId(propertyId);

        Booking booking = new Booking();
        booking.setStartDate(new Date());
        booking.setEndDate(new Date());
        booking.setDescription("Test Booking");

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(bookingRepository.findByPropertyId(propertyId)).thenReturn(Collections.singletonList(booking));

        String result = calendarService.getMyCalendar(propertyId);

        assertEquals(propertyId + ".ics", result);
        verify(propertyRepository, times(1)).save(property);
    }

    @Test
    void testGetMyCalendarShouldThrowWhenPropertyNotFound() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> calendarService.getMyCalendar(propertyId));
    }

    @Test
    void testSyncAirBnbCalendarSuccessfully() throws IOException, ParserException {
        Property property = new Property();
        property.setId(propertyId);
        property.setAirBnbICalUrl("https://bg.airbnb.com/calendar/ical/1247739757393025285.ics?s=743aa2e810329f14445dd5f53a6279f8");

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        calendarService.syncAirBnbCalendar(propertyId);

        verify(propertyRepository, times(1)).findById(propertyId);
    }

    @Test
    void testSyncForAvailableDatesReturnsTrue() {
        String filePath = "test.ics";
        Date startDateRequest = new Date();
        Date endDateRequest = new Date();

        File file = mock(File.class);
        when(file.exists()).thenReturn(true);

        boolean result = calendarService.syncForAvailableDates(filePath, startDateRequest, endDateRequest);

        assertTrue(result);
    }

    @Test
    void testSyncForAvailableDatesReturnFalse() {
        Date startDateRequest = new Date();
        Date endDateRequest = new Date();

        boolean result = calendarService.syncForAvailableDates(testFilePath, startDateRequest, endDateRequest);

        assertFalse(result);
    }

    @Test
    void testGetIcsFileSuccessfully() throws IOException {
        Property property = new Property();
        property.setId(propertyId);
        Booking booking = new Booking();
        booking.setStartDate(new Date());
        booking.setEndDate(new Date());
        booking.setDescription("Test Booking");
        booking.setUid("test-booking-uid");

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(bookingRepository.findByPropertyId(propertyId)).thenReturn(Collections.singletonList(booking));

        ResponseEntity<FileSystemResource> response = calendarService.getIcsFile(propertyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    private void createTestIcsFile(String filePath) throws IOException {
        Calendar calendar = new Calendar();
        ProdId prodId = new ProdId("//Reservation Platform//Hosting Calendar 1.0//EN");
        calendar.getProperties().add(prodId);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getProperties().add(Version.VERSION_2_0);

        VEvent event = new VEvent(new net.fortuna.ical4j.model.DateTime(new Date()),
                new net.fortuna.ical4j.model.DateTime(new Date(System.currentTimeMillis() + 3600000)),
                "Sample Event");
        event.getProperties().add(new Uid());
        calendar.getComponents().add(event);

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, outputStream);
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}