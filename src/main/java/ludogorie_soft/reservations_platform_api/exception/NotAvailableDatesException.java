package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class NotAvailableDatesException extends APIException {
    public NotAvailableDatesException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
