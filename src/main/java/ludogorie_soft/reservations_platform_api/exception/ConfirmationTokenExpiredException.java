package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class ConfirmationTokenExpiredException extends APIException{
    public ConfirmationTokenExpiredException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
