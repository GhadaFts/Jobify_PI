package com.jobify.interview.feign;

import com.jobify.interview.dto.ApplicationDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "application-service", url = "http://localhost:8888/application-service", path = "/api/applications")
public interface ApplicationFeignClient {

    @GetMapping("/{id}")
    ResponseEntity<ApplicationDetailsDTO> getById(
            @PathVariable String id,
            @RequestHeader("Authorization") String authorization);

    @PatchMapping("/{id}/status")
    ResponseEntity<Void> updateStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestHeader("Authorization") String authorization);

    @GetMapping("/{id}/validate")
    ResponseEntity<Boolean> validateApplicationExists(
            @PathVariable String id,
            @RequestHeader("Authorization") String authorization);
}