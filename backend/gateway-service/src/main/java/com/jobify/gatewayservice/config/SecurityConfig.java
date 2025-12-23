package com.jobify.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange
                        .anyExchange().permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // CRITICAL: Use setAllowedOriginPatterns instead of setAllowedOrigins
        // This is required when using setAllowCredentials(true)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:4200",      // Angular dev
                "http://localhost:*",          // Other local ports
                "http://127.0.0.1:*",         // Localhost IP
                "http://10.0.2.2:*",          // Android emulator
                "capacitor://localhost",       // Capacitor (Ionic)
                "ionic://localhost",           // Ionic
                "http://localhost"   ,
                "*"// Mobile local
        ));

        // CRITICAL: Must be true for Authorization headers
        configuration.setAllowCredentials(true);

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // CRITICAL: Allow all headers including Authorization
        configuration.setAllowedHeaders(List.of("*"));

        // Expose headers that frontend needs to read
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition",
                "Content-Type",
                "Content-Length",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        // Cache preflight for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Register for ALL paths including nested ones
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}