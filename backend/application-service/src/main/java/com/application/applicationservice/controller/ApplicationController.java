package com.application.applicationservice.controller;

import com.application.applicationservice.dto.ApplicationRequestDTO;
import com.application.applicationservice.dto.ApplicationResponseDTO;
import com.application.applicationservice.model.ApplicationStatus;
import com.application.applicationservice.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService service;

    // Only JOB_SEEKER can create applications
    @PostMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApplicationResponseDTO> create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ApplicationRequestDTO dto) {

        // Get job seeker ID from JWT token
        String jobSeekerId = jwt.getSubject(); // This is the keycloakId
        dto.setJobSeekerId(jobSeekerId); // Convert to Double

        return ResponseEntity.ok(service.create(dto));
    }

    // RECRUITER and ADMIN can view all applications
    @GetMapping
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<List<ApplicationResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // RECRUITER and JOB_SEEKER can view specific application
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'JOB_SEEKER', 'ADMIN')")
    public ResponseEntity<ApplicationResponseDTO> getById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id) {

        ApplicationResponseDTO application = service.getById(id);
        String userId = jwt.getSubject();

        // Check if user has access to this application
        if (hasRole(jwt, "RECRUITER") || hasRole(jwt, "ADMIN")) {
            // Recruiters and admins can view all
            return ResponseEntity.ok(application);
        } else if (hasRole(jwt, "JOB_SEEKER")) {
            // Job seekers can only view their own applications
            if (application.getJobSeekerId().equals(userId)) {
                return ResponseEntity.ok(application);
            } else {
                return ResponseEntity.status(403).build();
            }
        }

        return ResponseEntity.status(403).build();
    }

    // RECRUITER can view applications by job offer
    @GetMapping("/joboffer/{jobOfferId}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<List<ApplicationResponseDTO>> getByJobOfferId(
            @PathVariable Double jobOfferId) {
        return ResponseEntity.ok(service.getByJobOfferId(jobOfferId));
    }

    // JOB_SEEKER can view their own applications
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<List<ApplicationResponseDTO>> getMyApplications(
            @AuthenticationPrincipal Jwt jwt) {

        String jobSeekerId = jwt.getSubject();
        return ResponseEntity.ok(service.getByJobSeekerId(jobSeekerId));
    }

    // JOB_SEEKER can update their own application
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApplicationResponseDTO> updatePartial(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id,
            @RequestBody ApplicationRequestDTO dto) {

        // Verify ownership
        ApplicationResponseDTO application = service.getById(id);
        String userId = jwt.getSubject();

        if (!application.getJobSeekerId().equals(Double.parseDouble(userId.hashCode() + ""))) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(service.updatePartial(id, dto));
    }

    // Only RECRUITER can update application status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApplicationResponseDTO> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {

        String statusStr = payload.get("status");
        ApplicationStatus status = ApplicationStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    // Only RECRUITER and ADMIN can update AI score
    @PatchMapping("/{id}/ai-score")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<ApplicationResponseDTO> updateAiScore(
            @PathVariable String id,
            @RequestBody Map<String, Double> payload) {

        Double aiScore = payload.get("aiScore");
        return ResponseEntity.ok(service.updateAiScore(id, aiScore));
    }

    // JOB_SEEKER can delete their own application (if status allows)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id) {

        // Verify ownership
        ApplicationResponseDTO application = service.getById(id);
        String userId = jwt.getSubject();

        if (!application.getJobSeekerId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-duplicate")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Boolean> checkDuplicate(
            @RequestParam Double jobOfferId,
            @RequestParam String jobSeekerId) {
        return ResponseEntity.ok(service.checkDuplicateApplication(jobOfferId, jobSeekerId));
    }

    // Helper method to check if user has a specific role
    private boolean hasRole(Jwt jwt, String role) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") != null) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            return roles.contains(role);
        }
        return false;
    }
}