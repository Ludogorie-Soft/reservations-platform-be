package ludogorie_soft.reservations_platform_api.exception;

import org.springframework.http.HttpStatus;

public class InvalidCapacityException extends APIException{
    public InvalidCapacityException( String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
