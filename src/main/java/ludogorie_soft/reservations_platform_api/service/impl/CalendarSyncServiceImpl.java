package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.BookingRequestDto;
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
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarSyncServiceImpl implements CalendarSyncService {

    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;

    private static final String BASE_URL = "http://localhost:8080/calendar/ical/";

    @Value("${booking.ics.airBnb.directory}")
    private String icsAirBnbDirectory;

    @Value("${booking.ics.myCal.directory}")
    private String icsMyCalDirectory;

    public String createMyCalendar(Long propertyId, BookingRequestDto requestDto) throws IOException, URISyntaxException {

        Property property = getProperty(propertyId);
        List<Booking> bookings = bookingRepository.findByPropertyId(propertyId);

        Calendar calendar = new Calendar();
        ProdId prodId = new ProdId("X-RICAL-TZSOURCE=TZINFO:-//Reservation Platform//Hosting Calendar 1.0//EN");

        calendar.getProperties().add(prodId);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getProperties().add(Version.VERSION_2_0);

        for (Booking booking : bookings) {
            java.util.Calendar startCal = java.util.Calendar.getInstance();
            startCal.setTime(booking.getStartDate());

            java.util.Calendar endCal = java.util.Calendar.getInstance();
            endCal.setTime(booking.getEndDate());

            Date startDate = startCal.getTime();
            Date endDate = endCal.getTime();

            VEvent vEvent = new VEvent();
            vEvent.getProperties().add(new DtEnd(new net.fortuna.ical4j.model.Date(endDate)));
            vEvent.getProperties().add(new DtStart(new net.fortuna.ical4j.model.Date(startDate)));
            vEvent.getProperties().add(new Uid(property.getId() + "-" + booking.getId()));
            vEvent.getProperties().add(new Summary(booking.getDescription()));

            calendar.getComponents().add(vEvent);

            booking.setUid(vEvent.getUid().getValue());
            bookingRepository.save(booking);
        }

        File directory = new File(icsMyCalDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = propertyId + ".ics";
        String filePath = icsMyCalDirectory + File.separator + filename;
        String url = BASE_URL + filename;

        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        CalendarOutputter calendarOutputter = new CalendarOutputter();
        calendarOutputter.output(calendar, fileOutputStream);

        property.setSyncUrl(url);
        propertyRepository.save(property);

        return filename;
    }

    public void syncAirBnbCalendar(Long propertyId) throws IOException, ParserException {
        Property property = getProperty(propertyId);

        if (property.getAirBnbUrl() != null || property.getAirBnbUrl().trim().isEmpty()) {
            URL url = new URL(property.getAirBnbUrl());
            InputStream inputStream = url.openStream();

            CalendarBuilder calendarBuilder = new CalendarBuilder();
            Calendar calendar = calendarBuilder.build(inputStream);

            calendar.getComponents(Component.VEVENT).stream()
                    .map(VEvent.class::cast)
                    .filter(vEvent -> vEvent.getProperty(DtStamp.DTSTAMP) == null)
                    .forEach(vEvent -> vEvent.getProperties().add(new DtStamp()));

            File directory = new File(icsAirBnbDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filename = "airBnbCalendar-" + property.getId() + ".ics";
            String filePath = icsAirBnbDirectory + File.separator + filename;

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            CalendarOutputter calendarOutputter = new CalendarOutputter();
            calendarOutputter.output(calendar, fileOutputStream);
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

                    java.util.Date startDate = vEvent.getStartDate().getDate();
                    java.util.Date endDate = vEvent.getEndDate().getDate();

                    if (startDateRequest.equals(startDate) &&
                            startDate.equals(endDate)) {
                        return false;
                    }

                    if (startDateRequest.before(endDate) && endDateRequest.after(startDate)) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private Property getProperty(Long propertyId) {
        return propertyRepository.findById(propertyId).orElseThrow(
                () -> new IllegalArgumentException("Property not found!")
        );
    }
}

