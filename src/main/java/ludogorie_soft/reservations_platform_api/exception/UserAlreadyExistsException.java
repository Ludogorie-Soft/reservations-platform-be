package ludogorie_soft.reservations_platform_api.exception;


import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends APIException {
    public UserAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
