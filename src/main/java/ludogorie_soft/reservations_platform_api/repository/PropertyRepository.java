package ludogorie_soft.reservations_platform_api.repository;

import ludogorie_soft.reservations_platform_api.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {
}
