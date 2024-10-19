package ludogorie_soft.reservations_platform_api.exception;


import org.springframework.http.HttpStatus;

public class WrongPasswordException extends APIException {
    public WrongPasswordException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
