package com.jobify.interview.feign;

import com.jobify.interview.dto.UserDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "auth-service", path = "/api/users")
public interface UserFeignClient {

    @GetMapping("/{id}")
    ResponseEntity<UserDetailsDTO> getUserById(@PathVariable String id);

    @PostMapping("/batch")
    ResponseEntity<List<UserDetailsDTO>> getUsersByIds(@RequestBody List<String> ids);
}