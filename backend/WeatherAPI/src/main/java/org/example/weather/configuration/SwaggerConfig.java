package org.example.weather.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Weather API",
                version = "1.0",
                description = "A simple REST API for fetching weather data with proxy server",
                termsOfService = "Free plan",
                contact = @Contact(
                        name = "Andrey Rays",
                        email = "raisandrey@mail.ru",
                        url = "https://github.com/RaysAndRu"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(description = "Test server", url = "http://localhost:8080"),
                @Server(description = "Production server", url = "http://localhost:8080")
        },
        security = {
                @SecurityRequirement(name = "weatherAuth")
        }
)
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        name = "Bearer Authentication",
        in = SecuritySchemeIn.HEADER,
        description = "JWT Token"
)
public class SwaggerConfig {
}
