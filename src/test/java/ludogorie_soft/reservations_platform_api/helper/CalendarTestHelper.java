package ludogorie_soft.reservations_platform_api.helper;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class CalendarTestHelper {

    public static void createTestIcsFile(String filePath) throws IOException {
        File file = new File(filePath);
        File directory = file.getParentFile();

        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }

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

    public static void deleteDirectory(File directory) {
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
