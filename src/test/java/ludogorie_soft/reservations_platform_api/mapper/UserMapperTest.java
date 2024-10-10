package ludogorie_soft.reservations_platform_api.mapper;

import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.helper.UserTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private static final String TEST_NAME = "Test Name";
    private static final String TEST_EMAIL = "test@email.com";

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserMapper userMapper;

    private RegisterDto registerDto;
    private User user;

    @BeforeEach
    void setUp() {
        registerDto = UserTestHelper.createRegisterDto();
        user = UserTestHelper.createTestUser();
    }

    @Test
    void testShouldMapRegisterDtoToUserSuccessfully() {

        when(modelMapper.map(registerDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");

        User result = userMapper.toEntity(registerDto);

        assertNotNull(result);
        assertEquals(TEST_NAME, result.getName());
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals("encodedPassword", result.getPassword());

        verify(modelMapper).map(registerDto, User.class);
        verify(passwordEncoder).encode(registerDto.getPassword());
    }

    @Test
    void testShouldReturnNullWhenRegisterDtoIsNull() {

        User result = userMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void testShouldMapUserToRegisterDtoSuccessfully() {

        RegisterDto result = userMapper.toDto(user);

        assertNotNull(result);
        assertEquals(TEST_NAME, result.getName());
        assertEquals(TEST_EMAIL, result.getEmail());
    }
}