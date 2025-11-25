package com.application.applicationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "joboffer-service", url = "${joboffer.service.url:http://localhost:8082}")
public interface JobOfferFeignClient {
    
    @GetMapping("/api/joboffers/{id}")
    JobOfferDTO getJobOffer(@PathVariable("id") Integer id);
    
    @GetMapping("/api/joboffers/{id}/exists")
    Boolean validateJobExists(@PathVariable("id") Double id);
}