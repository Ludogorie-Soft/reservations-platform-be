package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class ConfirmationTokenNotValidException extends APIException{
    public ConfirmationTokenNotValidException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
