package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.ReservationResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Reservation;
import ludogorie_soft.reservations_platform_api.service.CalendarSyncService;
import ludogorie_soft.reservations_platform_api.service.ReservationService;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class CalendarSyncServiceImpl implements CalendarSyncService {

    private final ReservationService reservationService;
    private final ModelMapper modelMapper;

    @Override
    public ReservationResponseDto syncCalendar(String externalUrl) throws IOException, ParserException, URISyntaxException {

        URL url = new URL(externalUrl);

        InputStream inputStream = url.openStream();

        CalendarBuilder calendarBuilder = new CalendarBuilder();
        Calendar calendar = calendarBuilder.build(inputStream);

        ReservationResponseDto reservationResponseDto = new ReservationResponseDto();

        for (Iterator<CalendarComponent> iterator = calendar.getComponents().iterator(); iterator.hasNext(); ) {
            VEvent vEvent = (VEvent) iterator.next();

            String uid = vEvent.getUid().getValue(); // Uid
            String summary = vEvent.getSummary().getValue(); // Description
            java.util.Date startDate = vEvent.getStartDate().getDate(); // Start date
            java.util.Date endDate = vEvent.getEndDate().getDate(); // End date

            if (reservationService.existsByUid(uid)) {
                Reservation reservation = reservationService.findByUid(uid);
                reservationResponseDto = modelMapper.map(reservation, ReservationResponseDto.class);
//
//                if (!reservation.getStartDate().equals(startDate) ||
//                        !reservation.getEndDate().equals(endDate) ||
//                        !reservation.getDescription().equals(summary)) {
//
//                    // Update the existing reservation
//                    reservation.setStartDate(startDate);
//                    reservation.setEndDate(endDate);
//                    reservation.setDescription(summary);
//
//                    reservationService.updateReservation(reservation);
//                }
            } else {
                Reservation newReservation = new Reservation();
                newReservation.setUid(uid);
                newReservation.setStartDate(startDate);
                newReservation.setEndDate(endDate);
                newReservation.setDescription(summary);
                reservationResponseDto = reservationService.createReservation(newReservation);
            }
        }
        return reservationResponseDto;
    }
}
