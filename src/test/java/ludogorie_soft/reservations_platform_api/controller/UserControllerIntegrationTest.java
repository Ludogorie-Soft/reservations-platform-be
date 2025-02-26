package ludogorie_soft.reservations_platform_api.controller;

import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.dto.UserResponseDto;
import ludogorie_soft.reservations_platform_api.helper.UserTestHelper;
import ludogorie_soft.reservations_platform_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    public static final String USERS = "/user";

    private static final String REGISTER_URL = "/auth/register";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    private RegisterDto registerDto;

    @BeforeEach
    void setup() {
        registerDto = UserTestHelper.createRegisterDto();
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        // GIVEN
        createUserInDb();

        // WHEN
        ResponseEntity<List<UserResponseDto>> response = testRestTemplate.exchange(
                USERS,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isEmpty());
        assertTrue(response.getBody().stream()
                .anyMatch(user -> user.getEmail().equals(registerDto.getEmail())));
    }



    private ResponseEntity<String> createUserInDb() {
        return testRestTemplate
                .postForEntity(REGISTER_URL, registerDto, String.class);
    }

}
