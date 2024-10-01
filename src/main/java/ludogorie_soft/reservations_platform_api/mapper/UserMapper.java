package ludogorie_soft.reservations_platform_api.mapper;

import lombok.AllArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.entity.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private void configureMappings() {
        modelMapper.addMappings(new PropertyMap<RegisterDto, User>() {
            @Override
            protected void configure() {
                skip(destination.getPassword());
                skip(destination.getId());
            }
        });
    }

    public User toEntity(RegisterDto registerDto) {
        if (registerDto == null) {
            return null;
        }

        User user = modelMapper.map(registerDto, User.class);
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
