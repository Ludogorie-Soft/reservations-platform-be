package ludogorie_soft.reservations_platform_api.controller;

import ludogorie_soft.reservations_platform_api.helper.CalendarTestHelper;
import ludogorie_soft.reservations_platform_api.service.impl.CalendarServiceImpl;
import net.fortuna.ical4j.data.ParserException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CalendarControllerIntegrationTest {

    private static final String BASE_ICAL_URL = "/calendar/ical/";
    private static final String BASE_AIRBNB_ICAL_URL = "/calendar/ical/air-bnb/";
    private static final String TEST_FILE_PATH = "my-calendar.ics";
    private static final String TEST_AIRBNB_FILE_PATH = "airBnbCalendar-1.ics";

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private CalendarServiceImpl calendarService;

    private UUID propertyId;

    @BeforeEach
    void setup() {
        propertyId = UUID.randomUUID();
    }

    @AfterEach
    void cleanUp() {
        File testFile = new File(TEST_FILE_PATH);
        if (testFile.exists()) {
            testFile.delete();
        }
        File testAirBnbFile = new File(TEST_AIRBNB_FILE_PATH);
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
    void testGetIcsFileShouldThrowNotFound() throws IOException {
        // GIVEN
        when(calendarService.getIcsFile(Mockito.any(UUID.class))).thenReturn(ResponseEntity.notFound().build());

        // WHEN
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_ICAL_URL + propertyId, String.class);

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetIcsFileSuccessfully() throws IOException {
        // GIVEN
        CalendarTestHelper.createTestIcsFile(TEST_FILE_PATH);
        FileSystemResource fileResource = new FileSystemResource(TEST_FILE_PATH);
        when(calendarService.getIcsFile(Mockito.any(UUID.class))).thenReturn(ResponseEntity.ok(fileResource));

        // WHEN
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_ICAL_URL + propertyId, String.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetAirBnbIcsFileSuccessfully() throws IOException {
        // GIVEN
        CalendarTestHelper.createTestIcsFile(TEST_AIRBNB_FILE_PATH);
        FileSystemResource fileResource = new FileSystemResource(TEST_AIRBNB_FILE_PATH);
        when(calendarService.getAirBnbIcsFile(Mockito.any(UUID.class))).thenReturn(ResponseEntity.ok(fileResource));

        // WHEN
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_AIRBNB_ICAL_URL + propertyId, String.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetAirBnbIcsFileShouldThrowNotFound() {
        // GIVEN
        when(calendarService.getAirBnbIcsFile(Mockito.any(UUID.class))).thenReturn(ResponseEntity.notFound().build());

        // WHEN
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_AIRBNB_ICAL_URL + propertyId, String.class);

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testSyncAirbnbCalendarSuccessfully() throws IOException, ParserException {
        // GIVEN
        Mockito.doNothing().when(calendarService).syncAirBnbCalendar(Mockito.any(UUID.class));

        // WHEN
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_AIRBNB_ICAL_URL + propertyId + "/sync", String.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Calendar sync successfully!", response.getBody());
    }

    @Test
    void testSyncAirbnbCalendarShouldThrowInternalServerError() throws IOException, ParserException {
        // GIVEN
        Mockito.doThrow(new IOException("Sync failed")).when(calendarService).syncAirBnbCalendar(Mockito.any(UUID.class));

        // WHEN
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_AIRBNB_ICAL_URL + propertyId + "/sync", String.class);

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}