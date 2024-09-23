package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.service.CalendarSyncService;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalendarSyncServiceImpl implements CalendarSyncService {

    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;

    private static final String BASE_URL = "http://localhost:8080/calendar/ical/";

    @Value("${booking.ics.directory}")
    private String icsDirectory;

    public String syncMyCalendar(Long propertyId) throws IOException, URISyntaxException {
        List<Booking> bookings = bookingRepository.findByPropertyId(propertyId);

        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("No bookings found!");
        }

        Calendar calendar = new Calendar();
        ProdId prodId = new ProdId();
        prodId.setValue("//Reservation Platform//ver. 1.0//EN");
        calendar.getProperties().add(prodId);
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        for (Booking booking : bookings) {
            java.util.Calendar startCal = java.util.Calendar.getInstance();
            startCal.setTime(booking.getStartDate());

            java.util.Calendar endCal = java.util.Calendar.getInstance();
            endCal.setTime(booking.getEndDate());

            Date startDate = startCal.getTime();
            Date endDate = endCal.getTime();

            VEvent reservationEvent = new VEvent(
                    new net.fortuna.ical4j.model.Date(startDate),
                    new net.fortuna.ical4j.model.Date(endDate),
                    booking.getDescription());

            reservationEvent.getProperties().add(new Uid(booking.getUid()));
            reservationEvent.getProperties().add(new Organizer("mailto:" + booking.getEmail()));

            calendar.getComponents().add(reservationEvent);
        }

        File directory = new File(icsDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = propertyId + ".ics";
        String filePath = icsDirectory + File.separator + filename;
        String url = BASE_URL + filename;

        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        CalendarOutputter calendarOutputter = new CalendarOutputter();
        calendarOutputter.output(calendar, fileOutputStream);

        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new IllegalArgumentException("Property not found!")
        );

        property.setSyncUrl(url);

        return filename;
    }

    public void syncAirBnbCalendar(Long propertyId) throws IOException, ParserException {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new IllegalArgumentException("Property not found!")
        );

        if (property.getAirBnbUrl() != null || property.getAirBnbUrl().trim().isEmpty()) {
            URL url = new URL(property.getAirBnbUrl());
            InputStream inputStream = url.openStream();

            CalendarBuilder calendarBuilder = new CalendarBuilder();
            Calendar calendar = calendarBuilder.build(inputStream);

            calendar.getComponents(Component.VEVENT).stream()
                    .map(VEvent.class::cast)
                    .filter(vEvent -> vEvent.getProperty(DtStamp.DTSTAMP) == null)
                    .forEach(vEvent -> vEvent.getProperties().add(new DtStamp()));

            File directory = new File(icsDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filename = "airBnbCalendar.ics";
            String filePath = icsDirectory + File.separator + filename;

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            CalendarOutputter calendarOutputter = new CalendarOutputter();
            calendarOutputter.output(calendar, fileOutputStream);
        }
    }
}

