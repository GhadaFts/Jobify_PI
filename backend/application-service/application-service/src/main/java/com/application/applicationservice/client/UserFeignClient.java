package com.application.applicationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface UserFeignClient {

    @GetMapping("/auth/users/{keycloakId}")
    UserDTO getUser(@PathVariable("keycloakId") String keycloakId);

    @GetMapping("/auth/users/{keycloakId}/exists")
    Boolean validateUserExists(@PathVariable("keycloakId") String keycloakId);
}