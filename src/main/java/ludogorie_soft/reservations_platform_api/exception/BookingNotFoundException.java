package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class BookingNotFoundException extends APIException {
    public BookingNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
