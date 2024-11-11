package ludogorie_soft.reservations_platform_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Ludogorie Soft"
                ),
                description = "A backend rest api for handling reservations",
                title = "Reservations Platform BE",
                version = "0.1"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8082/"
                )
        }
)
public class OpenApiConfig {
}
