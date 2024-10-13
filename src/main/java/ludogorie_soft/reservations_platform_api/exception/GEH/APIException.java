package ludogorie_soft.reservations_platform_api.exception.GEH;


import org.springframework.http.HttpStatus;

public class APIException extends RuntimeException {
    private final HttpStatus status;

    public APIException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
