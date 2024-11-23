package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class ConfirmationTokenNotFoundException extends APIException{
    public ConfirmationTokenNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
