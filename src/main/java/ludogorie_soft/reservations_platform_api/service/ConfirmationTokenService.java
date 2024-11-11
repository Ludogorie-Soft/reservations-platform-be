package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.BookingResponseWithCustomerDataDto;
import ludogorie_soft.reservations_platform_api.entity.ConfirmationToken;

public interface ConfirmationTokenService {

    ConfirmationToken createConfirmationToken();

    void resetConfirmationToken(String email);

    ConfirmationToken getToken(String token);

    BookingResponseWithCustomerDataDto confirmReservation(String token);
}
