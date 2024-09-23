package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.entity.Booking;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.repository.BookingRepository;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.service.CalendarSyncService;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class CalendarSyncServiceImpl implements CalendarSyncService {

    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final PropertyRepository propertyRepository;

    @Override
    public String syncCalendar(Long propertyId) throws IOException, ParserException {

        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new IllegalArgumentException("Property not found!")
        );

        if (property.getAirBnbUrl() != null) {
            return syncCalendar(property.getBookingUrl());
        }
        return "No calendars to sync";
    }

    private String syncCalendar(String url1) throws IOException, ParserException {
        URL url = new URL(url1);
        InputStream inputStream = url.openStream();

        CalendarBuilder calendarBuilder = new CalendarBuilder();
        Calendar calendar = calendarBuilder.build(inputStream);

        for (Iterator<CalendarComponent> iterator = calendar.getComponents().iterator(); iterator.hasNext(); ) {
            VEvent vEvent = (VEvent) iterator.next();

            String uid = vEvent.getUid().getValue(); // Uid
            String summary = vEvent.getSummary().getValue(); // Description
            java.util.Date startDate = vEvent.getStartDate().getDate(); // Start date
            java.util.Date endDate = vEvent.getEndDate().getDate(); // End date

            if (bookingRepository.existsByUid(uid)) {
                Booking booking = bookingRepository.findByUid(uid);
                return "You have already reservation from " + booking.getStartDate() + " to " + booking.getEndDate();
            }
        }
        return "Available dates!";
    }
}
