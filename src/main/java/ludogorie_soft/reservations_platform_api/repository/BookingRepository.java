package ludogorie_soft.reservations_platform_api.repository;

import ludogorie_soft.reservations_platform_api.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByPropertyId(UUID id);

    @Query("SELECT b FROM Booking b JOIN FETCH b.property p " +
            "WHERE p.id = :propertyId " +
            "AND b.startDate < :endDate " +
            "AND b.endDate > :startDate")
    List<Booking> findBookingsByPropertyIdAndDateRange(@Param("propertyId") UUID propertyId,
                                                       @Param("startDate") Date startDate,
                                                       @Param("endDate") Date endDate);

    Optional<Booking> findByCustomerId(UUID id);

    Optional<Booking> findByConfirmationTokenId(UUID id);
}
