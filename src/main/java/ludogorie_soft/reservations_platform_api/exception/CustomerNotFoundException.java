package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class CustomerNotFoundException extends APIException {
    public CustomerNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
