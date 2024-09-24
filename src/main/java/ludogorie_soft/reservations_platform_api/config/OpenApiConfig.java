package ludogorie_soft.reservations_platform_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Ludogorie Soft",
                        email = "contacts@ludogoriesoft.com",
                        url = "https://ludogoriesoft.com/"
                ),
                description = "A backend rest api for handling reservations",
                title = "Reservations Platform BE",
                version = "0.1",
                license = @License(
                        name = "No license",
                        url = "license-url-placeholder.org"
                ),
                termsOfService = "Terms of Service"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080/"
                )
        }
//        security = {
//                @SecurityRequirement(
//                        name = "bearerAuth"
//                )
//        }
)
//@SecuritySchemes(
//        @SecurityScheme(
//                name = "bearerAuth",
//                description = "JWT auth description",
//                scheme = "bearer",
//                type = SecuritySchemeType.HTTP,
//                bearerFormat = "JWT",
//                in = SecuritySchemeIn.HEADER
//        )
//)
public class OpenApiConfig {
}
