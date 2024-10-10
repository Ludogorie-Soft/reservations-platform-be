package ludogorie_soft.reservations_platform_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReservationsPlatformApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationsPlatformApiApplication.class, args);
	}

}



