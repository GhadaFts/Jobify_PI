package com.jobify.interview.controller;

import com.jobify.interview.dto.InterviewRequestDTO;
import com.jobify.interview.dto.InterviewResponseDTO;
import com.jobify.interview.dto.InterviewUpdateDTO;
import com.jobify.interview.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class InterviewController {

    private final InterviewService interviewService;

    // Only RECRUITER can schedule interviews
    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<InterviewResponseDTO> scheduleInterview(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody InterviewRequestDTO requestDTO) {

        log.info("REST request to schedule interview for application: {}",
                requestDTO.getApplicationId());

        // Get recruiter ID from JWT
        String recruiterId = jwt.getSubject();
        requestDTO.setRecruiterId(recruiterId);

        InterviewResponseDTO response = interviewService.scheduleInterview(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // RECRUITER and JOB_SEEKER can view interview details
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'JOB_SEEKER')")
    public ResponseEntity<InterviewResponseDTO> getInterviewById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {

        log.info("REST request to get interview: {}", id);

        InterviewResponseDTO response = interviewService.getInterviewById(id);
        String userId = jwt.getSubject();

        // Check if user is involved in this interview
        if (response.getRecruiterId().equals(userId) ||
                response.getJobSeekerId().equals(userId) ||
                hasRole(jwt, "ADMIN")) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(403).build();
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'JOB_SEEKER')")
    public ResponseEntity<List<InterviewResponseDTO>> getInterviewsByApplicationId(
            @PathVariable String applicationId) {

        log.info("REST request to get interviews for application: {}", applicationId);

        List<InterviewResponseDTO> response =
                interviewService.getInterviewsByApplicationId(applicationId);
        return ResponseEntity.ok(response);
    }

    // JOB_SEEKER can view their own interviews
    @GetMapping("/my-interviews")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<List<InterviewResponseDTO>> getMyInterviews(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("REST request to get interviews for job seeker: {}", userId);

        List<InterviewResponseDTO> response = interviewService.getInterviewsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    // JOB_SEEKER can view their upcoming interviews
    @GetMapping("/my-interviews/upcoming")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<List<InterviewResponseDTO>> getMyUpcomingInterviews(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("REST request to get upcoming interviews for job seeker: {}", userId);

        List<InterviewResponseDTO> response = interviewService.getUpcomingInterviews(userId);
        return ResponseEntity.ok(response);
    }

    // RECRUITER can view their interviews
    @GetMapping("/recruiter/my-interviews")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<InterviewResponseDTO>> getRecruiterInterviews(
            @AuthenticationPrincipal Jwt jwt) {

        String recruiterId = jwt.getSubject();
        log.info("REST request to get interviews for recruiter: {}", recruiterId);

        List<InterviewResponseDTO> response =
                interviewService.getInterviewsByRecruiterId(recruiterId);
        return ResponseEntity.ok(response);
    }

    // RECRUITER can view their upcoming interviews
    @GetMapping("/recruiter/my-interviews/upcoming")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<InterviewResponseDTO>> getRecruiterUpcomingInterviews(
            @AuthenticationPrincipal Jwt jwt) {

        String recruiterId = jwt.getSubject();
        log.info("REST request to get upcoming interviews for recruiter: {}", recruiterId);

        List<InterviewResponseDTO> response =
                interviewService.getUpcomingInterviewsByRecruiterId(recruiterId);
        return ResponseEntity.ok(response);
    }

    // Only RECRUITER who created the interview can update it
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<InterviewResponseDTO> updateInterview(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody InterviewUpdateDTO updateDTO) {

        log.info("REST request to update interview: {}", id);

        // Check if recruiter owns this interview
        InterviewResponseDTO interview = interviewService.getInterviewById(id);
        String recruiterId = jwt.getSubject();

        if (!interview.getRecruiterId().equals(recruiterId) && !hasRole(jwt, "ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        InterviewResponseDTO response = interviewService.updateInterview(id, updateDTO);
        return ResponseEntity.ok(response);
    }

    // Only RECRUITER who created the interview can cancel it
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Void> cancelInterview(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {

        log.info("REST request to cancel interview: {}", id);

        // Check if recruiter owns this interview
        InterviewResponseDTO interview = interviewService.getInterviewById(id);
        String recruiterId = jwt.getSubject();

        if (!interview.getRecruiterId().equals(recruiterId) && !hasRole(jwt, "ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        interviewService.cancelInterview(id);
        return ResponseEntity.noContent().build();
    }

    // ADMIN only - send reminders
    @PostMapping("/reminders/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendInterviewReminders() {
        log.info("REST request to send interview reminders");

        interviewService.sendInterviewReminders();
        return ResponseEntity.ok().build();
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
