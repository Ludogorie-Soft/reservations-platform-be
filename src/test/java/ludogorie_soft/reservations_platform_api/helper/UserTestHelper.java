package ludogorie_soft.reservations_platform_api.helper;

import ludogorie_soft.reservations_platform_api.dto.LoginDto;
import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.entity.Role;
import ludogorie_soft.reservations_platform_api.entity.User;

import java.util.Set;

public class UserTestHelper {

    private static final String TEST_NAME = "Test Name";
    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_ROLE = "ROLE_OWNER";
    private static final String INTEGRATION_TEST_ROLE = "ROLE_TEST";

    private static final String EXPIRED_TEST_NAME = "Expired Test Name";
    private static final String EXPIRED_TEST_USERNAME = "expiredTestUsername";
    private static final String EXPIRED_TEST_EMAIL = "expiredtest@email.com";
    private static final String EXPIRED_TEST_PASSWORD = "expiredpassword";
    private static final String EXPIRED_INTEGRATION_TEST_ROLE = "ROLE_EXPIRED";

    public static RegisterDto createRegisterDto() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setName(TEST_NAME);
        registerDto.setUsername(TEST_USERNAME);
        registerDto.setEmail(TEST_EMAIL);
        registerDto.setPassword(TEST_PASSWORD);
        registerDto.setRepeatPassword(TEST_PASSWORD);
        return registerDto;
    }

    public static LoginDto createLoginDto() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail(TEST_EMAIL);
        loginDto.setPassword(TEST_PASSWORD);
        return loginDto;
    }

    public static Role createTestRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(TEST_ROLE);
        return role;
    }

    public static Role createRoleForIntegrationTest() {
        Role role = new Role();
        role.setName(INTEGRATION_TEST_ROLE);
        return role;
    }

    public static Role createExpiredRoleForIntegrationTest() {
        Role role = new Role();
        role.setName(EXPIRED_INTEGRATION_TEST_ROLE);
        return role;
    }

    public static User createTestUser() {
        User user = new User();
        user.setName(TEST_NAME);
        user.setUsername(TEST_USERNAME);
        user.setEmail(TEST_EMAIL);
        user.setId(1L);
        user.setPassword(TEST_PASSWORD);
        user.setRoles(Set.of(createTestRole()));

        return user;
    }

    public static User createUserForIntegrationTest() {
        User user = new User();

        user.setName(TEST_NAME);
        user.setUsername(TEST_USERNAME);
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);
        user.setRoles(Set.of(createRoleForIntegrationTest()));

        return user;
    }

    public static User createExpiredUserForIntegrationTest() {
        User user = new User();

        user.setName(EXPIRED_TEST_NAME);
        user.setUsername(EXPIRED_TEST_USERNAME);
        user.setEmail(EXPIRED_TEST_EMAIL);
        user.setPassword(EXPIRED_TEST_PASSWORD);
        user.setRoles(Set.of(createExpiredRoleForIntegrationTest()));

        return user;
    }
}
