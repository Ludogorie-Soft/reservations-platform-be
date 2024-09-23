package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.service.CalendarSyncService;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStamp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class CalendarSyncServiceImpl implements CalendarSyncService {

    private final PropertyRepository propertyRepository;

    @Value("${booking.ics.directory}")
    private String icsDirectory;

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

