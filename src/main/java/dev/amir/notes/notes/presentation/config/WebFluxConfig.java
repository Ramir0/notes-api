package dev.amir.notes.notes.presentation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * WebFlux Configuration
 * <p>
 * This configuration class sets up WebFlux-specific settings
 * including CORS, codecs, and functional routing.
 */
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    /**
     * Configure CORS for cross-origin requests
     */
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Configure server codecs for request/response processing
     */
    @Override
    public void configureHttpMessageCodecs(@NonNull ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(1024 * 1024); // 1MB
        configurer.defaultCodecs().enableLoggingRequestDetails(true);
    }

    /**
     * Functional route for health check
     */
    @Bean
    public RouterFunction<ServerResponse> healthCheckRoute() {
        return RouterFunctions
                .route(GET("/health"),
                        request -> ServerResponse.ok()
                                .bodyValue("Notes API is running"))
                .andRoute(GET("/"),
                        request -> ServerResponse.ok()
                                .bodyValue("Welcome to Notes API - Visit /swagger-ui.html for API documentation"));
    }
}
