package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.entity.Booking;

import java.net.URISyntaxException;

public interface IcsGeneratorService {
    String createCalendarEvent(Booking booking) throws URISyntaxException;
}
