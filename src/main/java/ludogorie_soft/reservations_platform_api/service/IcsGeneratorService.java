package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.entity.Reservation;

import java.net.URISyntaxException;

public interface IcsGeneratorService {

    String createCalendarFileForAllReservations();
    String createCalendarEvent(Reservation reservation) throws URISyntaxException;
}
