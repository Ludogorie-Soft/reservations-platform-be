package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class PetNotAllowedException extends APIException{
    public PetNotAllowedException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
