package ludogorie_soft.reservations_platform_api.repository;

import ludogorie_soft.reservations_platform_api.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByUid(String uid);
    Booking findByUid(String uid);

    @Query("SELECT b FROM Booking b WHERE b.startDate >= :startDate AND b.endDate <= :endDate")
    Booking findByStartDateAndEndDate(@Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate);
}
