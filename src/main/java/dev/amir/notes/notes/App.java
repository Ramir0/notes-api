package dev.amir.notes.notes;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

/**
 * Spring Boot WebFlux Simple Notes API Application
 * <p>
 * This application provides a reactive REST API for managing notes
 *
 * @author Simple Notes API Team
 * @version 1.0
 */
@SpringBootApplication
@EnableReactiveMongoAuditing
@OpenAPIDefinition(
        info = @Info(
                title = "Simple Notes API",
                version = "1.0.0",
                description = "Documentation for Simple Notes API",
                contact = @Contact(
                        name = "Amir Aranibar",
                        email = "amir.aranibar@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server")
        }
)
public class App {

    /**
     * Main method to run the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
