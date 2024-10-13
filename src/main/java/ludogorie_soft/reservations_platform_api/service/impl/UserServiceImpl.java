package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.AllArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.LoginDto;
import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.entity.Role;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.exception.APIException;
import ludogorie_soft.reservations_platform_api.mapper.UserMapper;
import ludogorie_soft.reservations_platform_api.repository.RoleRepository;
import ludogorie_soft.reservations_platform_api.repository.UserRepository;
import ludogorie_soft.reservations_platform_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String register(RegisterDto registerDto) {
        if (!registerDto.getPassword().equals(registerDto.getRepeatPassword())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Passwords do not match!");
        }
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Username already exists!");
        }
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Email already exists!");
        }
        User user = userMapper.toEntity(registerDto);
        Set<Role> roles = new HashSet<>();

        Optional<Role> optionalUserRole = Optional.ofNullable(roleRepository.findByName("ROLE_OWNER"));

        if (optionalUserRole.isPresent()) {
            roles.add(optionalUserRole.get());
        }
        user.setRoles(roles);
        userRepository.save(user);
        return "User Registered Successfully!";
    }

    //change after JWT token is added
    @Override
    public String login(LoginDto loginDto) {
        String usernameOrEmail = loginDto.getUsernameOrEmail();
        String password = loginDto.getPassword();
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (!userOptional.isPresent()) {
            return "Invalid username or email";
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid password";
        }
        return "Login successful";
    }

    @Override
    public User getUserByEmailOrUsername(String email, String username) {
        return userRepository
                .findByUsernameOrEmail(email, username)
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "User with this username or email not found!"));
    }
}
