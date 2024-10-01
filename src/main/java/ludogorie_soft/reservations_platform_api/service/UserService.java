package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.LoginDto;
import ludogorie_soft.reservations_platform_api.dto.RegisterDto;
import ludogorie_soft.reservations_platform_api.entity.User;

import java.util.Set;

public interface UserService {
    public String register(RegisterDto registerDto);
    public String login(LoginDto loginDto);

    User getUserByEmailOrUsername(String email, String username);

}
