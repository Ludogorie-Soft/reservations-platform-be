package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends APIException {
    public PasswordMismatchException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
