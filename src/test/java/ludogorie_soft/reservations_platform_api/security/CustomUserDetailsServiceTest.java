package ludogorie_soft.reservations_platform_api.security;

import ludogorie_soft.reservations_platform_api.entity.Role;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.helper.UserTestHelper;
import ludogorie_soft.reservations_platform_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "password";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = UserTestHelper.createTestUser();
        role = UserTestHelper.createTestRole();
    }

    @Test
    void testLoadUserByUsernameSuccessfully() {
        user.setRoles(Set.of(role));

        when(userRepository.findByUsernameOrEmail(TEST_EMAIL, TEST_EMAIL))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(TEST_EMAIL);

        assertNotNull(userDetails);
        assertEquals(TEST_EMAIL, userDetails.getUsername());
        assertEquals(TEST_PASSWORD, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_OWNER")));

        verify(userRepository, times(1)).findByUsernameOrEmail(TEST_EMAIL, TEST_EMAIL);
    }

    @Test
    void testLoadUserByUsernameShouldThrowWhenUserNotFound() {

        String email = "other@email.com";

        when(userRepository.findByUsernameOrEmail(email, email))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(email));

        assertEquals("User with this Username or Email not found.", exception.getMessage());

        verify(userRepository, times(1)).findByUsernameOrEmail(email, email);
    }

    @Test
    void testLoadUserByUsernameWithUserWithNoRoles() {
        user.setRoles(new HashSet<>());
        when(userRepository.findByUsernameOrEmail(TEST_EMAIL, TEST_EMAIL))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(TEST_EMAIL);

        assertNotNull(userDetails);
        assertEquals(TEST_EMAIL, userDetails.getUsername());
        assertEquals(TEST_PASSWORD, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(userRepository, times(1)).findByUsernameOrEmail(TEST_EMAIL, TEST_EMAIL);
    }

}