package ludogorie_soft.reservations_platform_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    private String name;
    private String type;

    private String airBnbICalUrl;
    private String bookingICalUrl;

    private String iCalSyncUrl;

    @OneToMany(mappedBy = "property")
    private List<Booking> bookings;

    private String websiteUrl;
    private int capacity;
    private boolean isPetAllowed;
    private String petRules;
    private int price;
}
