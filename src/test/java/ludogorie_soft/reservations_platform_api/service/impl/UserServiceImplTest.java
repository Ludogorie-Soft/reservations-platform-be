package ludogorie_soft.reservations_platform_api.service.impl;

import ludogorie_soft.reservations_platform_api.dto.LoginDto;
import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.dto.UserResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Role;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.exception.APIException;
import ludogorie_soft.reservations_platform_api.helper.UserTestHelper;
import ludogorie_soft.reservations_platform_api.mapper.UserMapper;
import ludogorie_soft.reservations_platform_api.repository.RoleRepository;
import ludogorie_soft.reservations_platform_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User createdUser;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_WhenPasswordsDoNotMatch_ThrowsAPIException() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setPassword("password");
        registerDto.setRepeatPassword("differentPassword");

        APIException exception = assertThrows(APIException.class, () -> userService.register(registerDto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Passwords do not match!", exception.getMessage());
    }

    @Test
    void register_WhenUsernameExists_ThrowsAPIException() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setPassword("password");
        registerDto.setRepeatPassword("password");

        when(userRepository.existsByUsername(any())).thenReturn(true);

        APIException exception = assertThrows(APIException.class, () -> userService.register(registerDto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Username already exists!", exception.getMessage());
    }

    @Test
    void register_WhenEmailExists_ThrowsAPIException() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("existingEmail@test.com");
        registerDto.setPassword("password");
        registerDto.setRepeatPassword("password");

        when(userRepository.existsByEmail("existingEmail@test.com")).thenReturn(true);

        APIException exception = assertThrows(APIException.class, () -> userService.register(registerDto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Email already exists!", exception.getMessage());
    }

    @Test
    void register_SuccessfulRegistration_ReturnsSuccessMessage() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("newUser");
        registerDto.setEmail("newUser@test.com");
        registerDto.setPassword("password");
        registerDto.setRepeatPassword("password");

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("newUser@test.com")).thenReturn(false);
        when(userMapper.toEntity(registerDto)).thenReturn(new User());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(new Role());

        String result = userService.register(registerDto);

        assertEquals("User Registered Successfully!", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_WhenUsernameOrEmailDoesNotExist_ReturnsInvalidUsernameOrEmail() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail("nonExistentUser");

        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());

        String result = userService.login(loginDto);

        assertEquals("Invalid username or email", result);
    }

    @Test
    void login_WhenPasswordIsIncorrect_ReturnsInvalidPassword() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail("existingUser");
        loginDto.setPassword("wrongPassword");

        User user = new User();
        user.setPassword("encodedPassword");

        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        String result = userService.login(loginDto);

        assertEquals("Invalid password", result);
    }

    @Test
    void login_SuccessfulLogin_ReturnsSuccessMessage() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail("existingUser");
        loginDto.setPassword("correctPassword");

        User user = new User();
        user.setPassword("encodedPassword");

        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);

        String result = userService.login(loginDto);

        assertEquals("Login successful", result);
    }

    @Test
    void getAllUsers_ShouldReturnUserResponseDtoList() {
        // GIVEN
        createdUser = UserTestHelper.createTestUser();
        userResponseDto = new UserResponseDto();
        userResponseDto.setId(createdUser.getId());
        userResponseDto.setName(createdUser.getName());
        userResponseDto.setUsername(createdUser.getUsername());
        userResponseDto.setEmail(createdUser.getEmail());

        List<User> users = List.of(createdUser);
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.when(modelMapper.map(createdUser, UserResponseDto.class)).thenReturn(userResponseDto);

        // WHEN
        List<UserResponseDto> result = userService.getAllUsers();

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(createdUser.getId(), result.get(0).getId());
        assertEquals(createdUser.getName(), result.get(0).getName());
        assertEquals(createdUser.getUsername(), result.get(0).getUsername());
        assertEquals(createdUser.getEmail(), result.get(0).getEmail());

        // Verify
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verify(modelMapper, Mockito.times(1)).map(createdUser, UserResponseDto.class);
    }
}

