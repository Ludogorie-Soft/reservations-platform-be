package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.exception.APIException;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.service.CalendarService;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;

    private static final String BASE_URL = "http://localhost:8080/calendar/ical/";

    @Value("${booking.ics.airBnb.directory}")
    private String icsAirBnbDirectory;

    @Value("${booking.ics.myCal.directory}")
    private String icsMyCalDirectory;

    public String getMyCalendar(Long propertyId) throws IOException {

        Property property = getProperty(propertyId);
        List<Booking> bookings = bookingRepository.findByPropertyId(propertyId);

        Calendar calendar = new Calendar();
        ProdId prodId = new ProdId("//Reservation Platform//Hosting Calendar 1.0//EN");

        calendar.getProperties().add(prodId);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getProperties().add(Version.VERSION_2_0);

        bookings.forEach(booking -> {
            VEvent vEvent = new VEvent(
                    new net.fortuna.ical4j.model.DateTime(booking.getStartDate()),
                    new net.fortuna.ical4j.model.DateTime(booking.getEndDate()),
                    booking.getDescription()
            );

            vEvent.getProperties().add(new Uid(booking.getUid()));
            calendar.getComponents().add(vEvent);

            booking.setUid(vEvent.getUid().getValue());
            bookingRepository.save(booking);
        });

        createDirectoryIfNotExists(icsMyCalDirectory);

        String filename = propertyId + ".ics";
        String filePath = icsMyCalDirectory + File.separator + filename;
        String url = BASE_URL + filename;

        createCalendarOutput(filePath, calendar);

        property.setICalSyncUrl(url);
        propertyRepository.save(property);

        return filename;
    }

    public void syncAirBnbCalendar(Long propertyId) throws IOException, ParserException {
        Property property = getProperty(propertyId);

        if (property.getAirBnbICalUrl() != null || property.getAirBnbICalUrl().trim().isEmpty()) {
            URL url = new URL(property.getAirBnbICalUrl());
            InputStream inputStream = url.openStream();

            CalendarBuilder calendarBuilder = new CalendarBuilder();
            Calendar calendar = calendarBuilder.build(inputStream);

            calendar.getComponents(Component.VEVENT).stream()
                    .map(VEvent.class::cast)
                    .filter(vEvent -> vEvent.getProperty(DtStamp.DTSTAMP) == null)
                    .forEach(vEvent -> vEvent.getProperties().add(new DtStamp()));

            createDirectoryIfNotExists(icsAirBnbDirectory);

            String filename = "airBnbCalendar-" + property.getId() + ".ics";
            String filePath = icsAirBnbDirectory + File.separator + filename;

            createCalendarOutput(filePath, calendar);
        }
    }

    @Override
    public boolean syncForAvailableDates(String filePath, Date startDateRequest, Date endDateRequest) {

        File file = new File(filePath);
        if (!file.exists()) {
            return true;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);

            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fileInputStream);

            for (CalendarComponent component : calendar.getComponents()) {
                if (component instanceof VEvent) {
                    VEvent vEvent = (VEvent) component;

                    if (startDateRequest.before(vEvent.getEndDate().getDate())
                            && endDateRequest.after(vEvent.getStartDate().getDate())) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return true;
    }

    @Override
    public ResponseEntity<FileSystemResource> getIcsFile(Long propertyId) throws IOException {

        String filename = getMyCalendar(propertyId);

        try {

            File file = new File(icsMyCalDirectory + File.separator + filename);

            if (file.exists()) {
                FileSystemResource resource = new FileSystemResource(file);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<FileSystemResource> getAirBnbIcsFile(Long propertyId) {

        String filename = "airBnbCalendar-" + propertyId + ".ics";

        try {

            File file = new File(icsAirBnbDirectory + File.separator + filename);

            if (file.exists()) {
                FileSystemResource resource = new FileSystemResource(file);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Property getProperty(Long propertyId) {
        return propertyRepository.findById(propertyId).orElseThrow(
                () -> new IllegalArgumentException("Property not found!")
        );
    }

    private static void createCalendarOutput(String filePath, Calendar calendar) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        CalendarOutputter calendarOutputter = new CalendarOutputter();
        calendarOutputter.output(calendar, fileOutputStream);
    }

    private void createDirectoryIfNotExists(String icsMyCalDirectory) {
        File directory = new File(icsMyCalDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}

