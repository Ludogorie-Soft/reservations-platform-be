package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.service.IcsGeneratorService;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IcsGeneratorServiceImpl implements IcsGeneratorService {

    private static final String BASE_URL = "http://localhost:8080/calendar/ical/";

    private final BookingRepository bookingRepository;

    @Value("${booking.ics.directory}")
    private String icsDirectory;

    public String getUrlForBooking(String uid) {

        Booking booking = bookingRepository.findByUid(uid);

        if (booking != null) {
            return booking.getUrl();
        } else {
            return "No matching bookings!";
        }
    }

    public String createCalendarEvent(Booking booking) throws URISyntaxException {

        try {
            Calendar calendar = new Calendar();
            ProdId prodId = new ProdId();
            prodId.setValue("//Reservation Platform//ver. 1.0//EN");
            calendar.getProperties().add(prodId);
            calendar.getProperties().add(Version.VERSION_2_0);
            calendar.getProperties().add(CalScale.GREGORIAN);

            java.util.Calendar startCal = java.util.Calendar.getInstance();
            startCal.setTime(booking.getStartDate());

            java.util.Calendar endCal = java.util.Calendar.getInstance();
            endCal.setTime(booking.getEndDate());

            // Make the last day inclusive
//        endCal.add(java.util.Calendar.DATE, 1);

            Date startDate = startCal.getTime();
            Date endDate = endCal.getTime();

            // Define a full-day event (without time)
            VEvent reservationEvent = new VEvent(
                    new net.fortuna.ical4j.model.Date(startDate),
                    new net.fortuna.ical4j.model.Date(endDate),
                    booking.getDescription());

            // Add properties to event
            reservationEvent.getProperties().add(new Uid(UUID.randomUUID() + "-" + booking.getId()));
            reservationEvent.getProperties().add(new Organizer("mailto:" + booking.getEmail()));

            // Add event to calendar
            calendar.getComponents().add(reservationEvent);

            // Write to .ics file
            File directory = new File(icsDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filename = reservationEvent.getUid().getValue() + ".ics";
            String filePath = icsDirectory + File.separator + filename;
            String url = BASE_URL + filename;

            // Set Uid and Url to reservation entity
            booking.setUid(reservationEvent.getUid().getValue());
            booking.setUrl(url);
            bookingRepository.save(booking);

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            CalendarOutputter calendarOutputter = new CalendarOutputter();
            calendarOutputter.output(calendar, fileOutputStream);

            return filename;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
