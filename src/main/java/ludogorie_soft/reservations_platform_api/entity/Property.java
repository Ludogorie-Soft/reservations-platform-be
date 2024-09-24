package ludogorie_soft.reservations_platform_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    private String name;
    private String type;

    private String airBnbUrl;
    private String bookingUrl;

    private String syncUrl;

    @OneToMany(mappedBy = "property")
    private List<Booking> bookings;


}
