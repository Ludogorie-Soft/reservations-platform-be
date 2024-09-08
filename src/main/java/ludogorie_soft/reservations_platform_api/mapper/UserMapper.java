package ludogorie_soft.reservations_platform_api.mapper;

import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(RegisterDto registerDto) {
        if (registerDto == null) {
            return null;
        }
        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setUsername(registerDto.getUserName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        return user;
    }

    public RegisterDto toDto(User user) {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setName(user.getName());
        registerDto.setEmail(user.getEmail());
        return registerDto;
    }
}
