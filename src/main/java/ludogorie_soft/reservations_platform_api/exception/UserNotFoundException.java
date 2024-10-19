package ludogorie_soft.reservations_platform_api.exception;


import org.springframework.http.HttpStatus;

// Custom exceptions
public class UserNotFoundException extends APIException {
    public UserNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
