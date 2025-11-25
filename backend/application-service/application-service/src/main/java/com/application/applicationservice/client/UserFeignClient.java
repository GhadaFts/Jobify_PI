package com.application.applicationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "jobseeker-service", url = "${jobseeker.service.url:http://localhost:8081}")
public interface UserFeignClient {
    
    @GetMapping("/api/jobseekers/{id}")
    UserDTO getUser(@PathVariable("id") Integer id);
    
    @GetMapping("/api/jobseekers/{id}/exists")
    Boolean validateUserExists(@PathVariable("id") Double id);
}