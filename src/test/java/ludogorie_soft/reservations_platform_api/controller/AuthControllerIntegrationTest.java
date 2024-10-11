package ludogorie_soft.reservations_platform_api.controller;

import ludogorie_soft.reservations_platform_api.dto.LoginDto;
import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.helper.UserTestHelper;
import ludogorie_soft.reservations_platform_api.mapper.UserMapper;
import ludogorie_soft.reservations_platform_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    private static final String LOGIN_URL = "/auth/login";

    private static final String REGISTER_URL = "/auth/register";

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    private RegisterDto registerDto;
    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        registerDto = UserTestHelper.createRegisterDto();
        loginDto = UserTestHelper.createLoginDto();
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUserSuccessfully() {
        //WHEN
        ResponseEntity<String> response = createUserInDB(registerDto);
        String responseMessage = response.getBody();

        //THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(responseMessage);
        assertEquals("User Registered Successfully!", responseMessage);
    }

    @Test
    void testRegisterUserShouldThrowWhenUsernameExists() {
        //GIVEN
        createUserInDB(registerDto);
        registerDto.setEmail("new@email.com");

        //WHEN
        ResponseEntity<String> createUserWithSameUsername = testRestTemplate
                .postForEntity(REGISTER_URL, registerDto, String.class);

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, createUserWithSameUsername.getStatusCode());
    }

    @Test
    void testRegisterUserShouldThrowWhenEmailExists() {
        //GIVEN
        createUserInDB(registerDto);
        registerDto.setUsername("newUsername");

        //WHEN
        ResponseEntity<String> createUserWithSameEmail = testRestTemplate
                .postForEntity(REGISTER_URL, registerDto, String.class);

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, createUserWithSameEmail.getStatusCode());
    }

    @Test
    void testRegisterUserShouldThrowWhenPasswordNotMatch() {
        //GIVEN
        registerDto.setRepeatPassword("otherPassword");

        //WHEN
        ResponseEntity<String> response = testRestTemplate
                .postForEntity(REGISTER_URL, registerDto, String.class);

        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testLoginSuccessfully() {
        //GIVEN
        createUserInDB(registerDto);

        //WHEN
        ResponseEntity<String> response = testRestTemplate
                .postForEntity(LOGIN_URL, loginDto, String.class);

        //THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testLoginShouldThrowWhenUserNotExists() {
        //GIVEN
        createUserInDB(registerDto);
        loginDto.setUsernameOrEmail("new@email.com");

        //WHEN
        ResponseEntity<String> response = testRestTemplate
                .postForEntity(LOGIN_URL, loginDto, String.class);

        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testLoginShouldThrowWhenPasswordNotMatch() {
        //GIVEN
        createUserInDB(registerDto);
        loginDto.setPassword("invalidPassword");

        //WHEN
        ResponseEntity<String> response = testRestTemplate
                .postForEntity(LOGIN_URL, loginDto, String.class);

        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private ResponseEntity<String> createUserInDB(RegisterDto registerDto) {
        return testRestTemplate.postForEntity(REGISTER_URL, registerDto, String.class);
    }
}