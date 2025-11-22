package com.jobify.interview.feign;

import com.jobify.interview.dto.ApplicationDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "application-service", path = "/api/applications")
public interface ApplicationFeignClient {

    @GetMapping("/{id}")
    ResponseEntity<ApplicationDetailsDTO> getApplicationById(@PathVariable Long id);

    @PutMapping("/{id}/status")
    ResponseEntity<Void> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status
    );

    @GetMapping("/{id}/validate")
    ResponseEntity<Boolean> validateApplicationExists(@PathVariable Long id);
}