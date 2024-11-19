package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class ConfirmationTokenAlreadyConfirmedException extends APIException{
    public ConfirmationTokenAlreadyConfirmedException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
