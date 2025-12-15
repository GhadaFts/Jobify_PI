package com.jobify.gatewayservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fallback")
public class FallBackController {
    @GetMapping("/interview")
    public ResponseEntity<String> interviewFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Interview service is temporarily unavailable");
    }
    @GetMapping("/ai-service")
    public ResponseEntity<String> aiServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("AI service is temporarily unavailable");
    }
    @GetMapping("/auth-service")
    public ResponseEntity<String> authServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Auth service is temporarily unavailable");
    }
    @GetMapping("/application-service")
    public ResponseEntity<String> applicationServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Application service is temporarily unavailable");
    }
    @GetMapping("/joboffer-service")
    public ResponseEntity<String> jobofferServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Joboffer service is temporarily unavailable");
    }
    @GetMapping("/career-advice")
    public ResponseEntity<String> careerAdviceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Career Advice service is temporarily unavailable");
    }
}

