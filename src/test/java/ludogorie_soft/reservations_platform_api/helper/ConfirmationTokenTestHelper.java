package ludogorie_soft.reservations_platform_api.helper;

import ludogorie_soft.reservations_platform_api.entity.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConfirmationTokenTestHelper {

    private static final Long TOKEN_ID = 1L;
    private static final String TOKEN = UUID.randomUUID().toString();
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final LocalDateTime EXPIRES_AT = LocalDateTime.now().plusMinutes(30);

    private static final Long EXPIRED_TOKEN_ID = 2L;
    private static final String EXPIRED_TOKEN = UUID.randomUUID().toString();
    private static final LocalDateTime EXPIRED_CREATED_AT = LocalDateTime.now().minusMinutes(31);
    private static final LocalDateTime EXPIRED_EXPIRES_AT = LocalDateTime.now().minusMinutes(1);

    public static ConfirmationToken createConfirmationToken() {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setId(TOKEN_ID);
        confirmationToken.setToken(TOKEN);
        confirmationToken.setCreatedAt(CREATED_AT);
        confirmationToken.setExpiresAt(EXPIRES_AT);

        return confirmationToken;
    }

    public static ConfirmationToken createExpiredConfirmationToken() {
        ConfirmationToken confirmationToken = createConfirmationToken();
        confirmationToken.setId(EXPIRED_TOKEN_ID);
        confirmationToken.setToken(EXPIRED_TOKEN);
        confirmationToken.setCreatedAt(EXPIRED_CREATED_AT);
        confirmationToken.setExpiresAt(EXPIRED_EXPIRES_AT);

        return confirmationToken;
    }

}