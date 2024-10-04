package ludogorie_soft.reservations_platform_api.data;
import jakarta.annotation.PostConstruct;
import ludogorie_soft.reservations_platform_api.entity.Role;
import ludogorie_soft.reservations_platform_api.repository.RoleRepository;
import org.springframework.stereotype.Component;

@Component

public class DataLoader {

    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

//    @PostConstruct
//    public void loadRoles() {
//        if (roleRepository.findByName("ADMIN") == null) {
//            Role adminRole = new Role();
//            adminRole.setName("ADMIN");
//            roleRepository.save(adminRole);
//        }
//
//        if (roleRepository.findByName("USER") == null) {
//            Role userRole = new Role();
//            userRole.setName("USER");
//            roleRepository.save(userRole);
//        }
//    }
}


