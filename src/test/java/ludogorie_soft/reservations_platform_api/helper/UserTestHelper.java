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
    private static final String TEST_PASSWORD = "password1234";
    private static final String TEST_ROLE = "ROLE_OWNER";


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

        return user;
    }
}
