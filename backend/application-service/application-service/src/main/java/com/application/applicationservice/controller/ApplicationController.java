package com.application.applicationservice.controller;

import com.application.applicationservice.dto.ApplicationRequestDTO;
import com.application.applicationservice.dto.ApplicationResponseDTO;
import com.application.applicationservice.model.ApplicationStatus;
import com.application.applicationservice.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService service;

    @PostMapping
    public ResponseEntity<ApplicationResponseDTO> create(@RequestBody ApplicationRequestDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/joboffer/{jobOfferId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getByJobOfferId(@PathVariable Double jobOfferId) {
        return ResponseEntity.ok(service.getByJobOfferId(jobOfferId));
    }

    @GetMapping("/jobseeker/{jobSeekerId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getByJobSeekerId(@PathVariable Double jobSeekerId) {
        return ResponseEntity.ok(service.getByJobSeekerId(jobSeekerId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApplicationResponseDTO> updatePartial(
            @PathVariable String id,
            @RequestBody ApplicationRequestDTO dto) {
        return ResponseEntity.ok(service.updatePartial(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponseDTO> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        String statusStr = payload.get("status");
        ApplicationStatus status = ApplicationStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @PatchMapping("/{id}/ai-score")
    public ResponseEntity<ApplicationResponseDTO> updateAiScore(
            @PathVariable String id,
            @RequestBody Map<String, Double> payload) {
        Double aiScore = payload.get("aiScore");
        return ResponseEntity.ok(service.updateAiScore(id, aiScore));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/check-duplicate")
    public ResponseEntity<Boolean> checkDuplicate(
            @RequestParam Double jobOfferId,
            @RequestParam Double jobSeekerId) {
        return ResponseEntity.ok(service.checkDuplicateApplication(jobOfferId, jobSeekerId));
    }
}