package ludogorie_soft.reservations_platform_api.repository;

import ludogorie_soft.reservations_platform_api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByAllFields(String firstName, String lastName, String email, String phoneNumber);
    Optional<Customer> findByEmail(String email);
}
