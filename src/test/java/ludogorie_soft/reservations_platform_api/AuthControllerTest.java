package ludogorie_soft.reservations_platform_api;


import ludogorie_soft.reservations_platform_api.controller.AuthController;
import ludogorie_soft.reservations_platform_api.dto.LoginDto;
import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_SuccessfulRegistration_ReturnsCreated() {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        when(userService.register(registerDto)).thenReturn("User Registered Successfully!");

        // Act
        ResponseEntity<String> response = authController.register(registerDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User Registered Successfully!", response.getBody());
    }

    @Test
    void login_SuccessfulLogin_ReturnsOk() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        when(userService.login(loginDto)).thenReturn("Login successful");

        // Act
        ResponseEntity<String> response = authController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody());
    }

    @Test
    void login_InvalidLogin_ReturnsUnauthorized() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        when(userService.login(loginDto)).thenReturn("Invalid username or password");

        // Act
        ResponseEntity<String> response = authController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }
}
