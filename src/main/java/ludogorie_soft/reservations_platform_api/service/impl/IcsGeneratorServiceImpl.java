package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.entity.Reservation;
import ludogorie_soft.reservations_platform_api.repository.ReservationRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class IcsGeneratorServiceImpl implements IcsGeneratorService {

    // Read the directory path from application.properties
    @Value("${reservation.ics.directory}")
    private String icsDirectory;

    private final ReservationRepository reservationRepository;

    public String createCalendarFileForAllReservations() {

        List<Reservation> reservations = reservationRepository.findAll();

        try {
            Calendar calendar = new Calendar();
            ProdId prodId = new ProdId();
            prodId.setValue("//Reservation Platform//ver. 1.0//EN");
            calendar.getProperties().add(prodId);
            calendar.getProperties().add(Version.VERSION_2_0);
            calendar.getProperties().add(CalScale.GREGORIAN);

            for (Reservation reservation : reservations) {
                java.util.Calendar startCal = java.util.Calendar.getInstance();
                startCal.setTime(reservation.getStartDate());
                java.util.Calendar endCal = java.util.Calendar.getInstance();
                endCal.setTime(reservation.getEndDate());
//                endCal.add(java.util.Calendar.DATE, 1);

                VEvent reservationEvent = new VEvent(
                        new net.fortuna.ical4j.model.Date(startCal.getTime()),
                        new net.fortuna.ical4j.model.Date(endCal.getTime()),
                        reservation.getDescription());

                reservationEvent.getProperties().add(new Uid("reservation-" + reservation.getId()));
                reservationEvent.getProperties().add(new Description(reservation.getDescription()));
                reservationEvent.getProperties().add(new Organizer("mailto:" + reservation.getEmail()));

                calendar.getComponents().add(reservationEvent);
            }

            // Define the file path
            String filename = "platform-calendar.ics";
            String filePath = icsDirectory + File.separator + filename;

            // Write the calendar to an .ics file
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                CalendarOutputter calendarOutputter = new CalendarOutputter();
                calendarOutputter.output(calendar, fileOutputStream);
            }

            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String createCalendarEvent(Reservation reservation) throws URISyntaxException {

        try {
            Calendar calendar = new Calendar();
            ProdId prodId = new ProdId();
            prodId.setValue("//Reservation Platform//ver. 1.0//EN");
            calendar.getProperties().add(prodId);
            calendar.getProperties().add(Version.VERSION_2_0);
            calendar.getProperties().add(CalScale.GREGORIAN);

            java.util.Calendar startCal = java.util.Calendar.getInstance();
            startCal.setTime(reservation.getStartDate());

            java.util.Calendar endCal = java.util.Calendar.getInstance();
            endCal.setTime(reservation.getEndDate());

            // Make the last day inclusive
//        endCal.add(java.util.Calendar.DATE, 1);

            Date startDate = startCal.getTime();
            Date endDate = endCal.getTime();

            // Define a full-day event (without time)
            VEvent reservationEvent = new VEvent(
                    new net.fortuna.ical4j.model.Date(startDate),
                    new net.fortuna.ical4j.model.Date(endDate),
                    reservation.getDescription());

            reservationEvent.getProperties().add(new Uid("reservation-" + reservation.getId()));
            reservationEvent.getProperties().add(new Description(reservation.getDescription()));
            reservationEvent.getProperties().add(new Organizer("mailto:" + reservation.getEmail()));

            // Add event to calendar
            calendar.getComponents().add(reservationEvent);

            // Set Uid to reservation
            reservation.setUid(reservationEvent.getUid().getValue());
            reservationRepository.save(reservation);

            // Write to .ics file
            File directory = new File(icsDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filename = "reservation-" + reservation.getId() + ".ics";
            String filePath = icsDirectory + File.separator + filename;


            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            CalendarOutputter calendarOutputter = new CalendarOutputter();
            calendarOutputter.output(calendar, fileOutputStream);

            return filename;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
