package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class InvalidDateRequestExceptionException extends APIException {
    public InvalidDateRequestExceptionException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
