package ludogorie_soft.reservations_platform_api.controller;

import ludogorie_soft.reservations_platform_api.dto.LoginDto;
import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.helper.UserTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthControllerIntegrationTest extends IntegrationTestBase {

    private String getLoginUrl() {
        return "/auth/login";
    }

    private String getRegisterUrl() {
        return "/auth/register";
    }

    private RegisterDto registerDto;
    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        registerDto = UserTestHelper.createRegisterDto();
        loginDto = UserTestHelper.createLoginDto();
    }

    @Test
    void testRegisterUserSuccessfully() {

        ResponseEntity<String> response = createUserInDB(registerDto);

        String responseMessage = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(responseMessage);
        assertEquals("User Registered Successfully!", responseMessage);
    }

    @Test
    void testRegisterUserShouldThrowWhenUsernameExists() {
        createUserInDB(registerDto);
        registerDto.setEmail("new@email.com");

        ResponseEntity<String> createUserWithSameUsername = testRestTemplate
                .postForEntity(getRegisterUrl(), registerDto, String.class);

        //TODO:
//        assertEquals(HttpStatus.BAD_REQUEST, createUserWithSameEmail.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, createUserWithSameUsername.getStatusCode());
    }

    @Test
    void testRegisterUserShouldThrowWhenEmailExists() {
        createUserInDB(registerDto);
        registerDto.setUsername("newUsername");

        ResponseEntity<String> createUserWithSameEmail = testRestTemplate
                .postForEntity(getRegisterUrl(), registerDto, String.class);

        //TODO:
//        assertEquals(HttpStatus.BAD_REQUEST, createUserWithSameEmail.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, createUserWithSameEmail.getStatusCode());
    }

    @Test
    void testRegisterUserShouldThrowWhenPasswordNotMatch() {
        registerDto.setRepeatPassword("otherPassword");

        ResponseEntity<String> response = testRestTemplate
                .postForEntity(getRegisterUrl(), registerDto, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testLoginSuccessfully() {
        createUserInDB(registerDto);

        ResponseEntity<String> response = testRestTemplate
                .postForEntity(getLoginUrl(), loginDto, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private ResponseEntity<String> createUserInDB(RegisterDto registerDto) {
        return testRestTemplate.postForEntity(getRegisterUrl(), registerDto, String.class);
    }
}