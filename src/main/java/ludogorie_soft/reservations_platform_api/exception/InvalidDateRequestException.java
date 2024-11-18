package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class InvalidDateRequestException extends APIException {
    public InvalidDateRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
