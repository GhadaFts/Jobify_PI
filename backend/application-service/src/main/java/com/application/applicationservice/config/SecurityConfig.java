package com.application.applicationservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }

    static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        private final Logger log = LoggerFactory.getLogger(KeycloakRoleConverter.class);

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            // Log token claims briefly for debugging (remove in production)
            try {
                if (log.isDebugEnabled()) {
                    log.debug("JWT claims: {}", jwt.getClaims());
                }
            } catch (Exception ignored) {
            }

            // Collect roles from several possible Keycloak claim locations
            Set<String> roles = Stream.of(
                            extractRealmRoles(jwt),
                            extractResourceRoles(jwt),
                            extractSimpleRolesClaim(jwt)
                    )
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            if (roles.isEmpty()) {
                return List.of();
            }

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        }

        private Set<String> extractRealmRoles(Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || realmAccess.get("roles") == null) {
                return Set.of();
            }

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            return Set.copyOf(roles);
        }

        @SuppressWarnings("unchecked")
        private Set<String> extractResourceRoles(Jwt jwt) {
            Object resourceAccessObj = jwt.getClaim("resource_access");
            if (!(resourceAccessObj instanceof Map)) {
                return Set.of();
            }
            Map<String, Object> resourceAccess = (Map<String, Object>) resourceAccessObj;
            return resourceAccess.values().stream()
                    .filter(v -> v instanceof Map)
                    .map(v -> (Map<String, Object>) v)
                    .filter(m -> m.get("roles") != null)
                    .flatMap(m -> ((List<String>) m.get("roles")).stream())
                    .collect(Collectors.toSet());
        }

        private Set<String> extractSimpleRolesClaim(Jwt jwt) {
            Object rolesObj = jwt.getClaim("roles");
            if (!(rolesObj instanceof List)) {
                return Set.of();
            }
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) rolesObj;
            return Set.copyOf(roles);
        }
    }
}