package com.joboffer.jobofferservice.controller;

import com.joboffer.jobofferservice.dto.JobOfferDTO;
import com.joboffer.jobofferservice.model.JobOffer;
import com.joboffer.jobofferservice.service.JobOfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobOfferController {

    private final JobOfferService jobOfferService;

    /**
     * Search jobs with filters - accessible to all authenticated users
     * GET /api/jobs
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('RECRUITER', 'JOB_SEEKER', 'ADMIN')")
    public ResponseEntity<List<JobOfferDTO>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String location) {

        log.info("REST request to search jobs with filters - title: {}, type: {}, experience: {}, location: {}",
                title, type, experience, location);

        Specification<JobOffer> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (type != null && !type.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("type"), type));
        }
        if (experience != null && !experience.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("experience"), experience));
        }
        if (location != null && !location.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
        }

        List<JobOfferDTO> jobs = jobOfferService.searchJobs(spec);
        return ResponseEntity.ok(jobs);
    }

    /**
     * Get job by ID - accessible to all authenticated users
     * GET /api/jobs/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'JOB_SEEKER', 'ADMIN')")
    public ResponseEntity<JobOfferDTO> getJobById(@PathVariable Long id) {
        log.info("REST request to get job by ID: {}", id);

        JobOfferDTO job = jobOfferService.getJobById(id);
        return ResponseEntity.ok(job);
    }

    /**
     * Create new job - only RECRUITER and ADMIN can create jobs
     * POST /api/jobs
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<JobOfferDTO> createJob(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody JobOfferDTO jobOfferDTO) {

        String recruiterId = jwt.getSubject();
        log.info("REST request to create job by recruiter: {}", recruiterId);

        // Set the recruiter ID from JWT
        jobOfferDTO.setRecruiterId(recruiterId);

        // Log incoming company logo (helps debug missing logo in DB)
        log.info("Incoming companyLogo for create: {}", jobOfferDTO.getCompanyLogo());

        jobOfferService.validateJobData(jobOfferDTO);
        JobOfferDTO createdJob = jobOfferService.createJob(jobOfferDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
    }

    /**
     * Update job - only RECRUITER who created it or ADMIN can update
     * PUT /api/jobs/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<JobOfferDTO> updateJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody JobOfferDTO jobOfferDTO) {

        String userId = jwt.getSubject();
        log.info("REST request to update job: {} by user: {}", id, userId);

        // Get existing job to check ownership
        JobOfferDTO existingJob = jobOfferService.getJobById(id);

        // Check if user owns this job or is an admin
        if (!existingJob.getRecruiterId().equals(userId) && !hasRole(jwt, "ADMIN")) {
            log.warn("User {} attempted to update job {} without permission", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        JobOfferDTO updatedJob = jobOfferService.updateJob(id, jobOfferDTO);
        return ResponseEntity.ok(updatedJob);
    }

    /**
     * Delete job - only RECRUITER who created it or ADMIN can delete
     * DELETE /api/jobs/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> deleteJob(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {

        String userId = jwt.getSubject();
        log.info("REST request to delete job: {} by user: {}", id, userId);

        // Get existing job to check ownership
        JobOfferDTO existingJob = jobOfferService.getJobById(id);

        // Check if user owns this job or is an admin
        if (!existingJob.getRecruiterId().equals(userId) && !hasRole(jwt, "ADMIN")) {
            log.warn("User {} attempted to delete job {} without permission", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        jobOfferService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get jobs created by the logged-in recruiter
     * GET /api/jobs/my-jobs
     */
    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<JobOfferDTO>> getMyJobs(
            @AuthenticationPrincipal Jwt jwt) {

        String recruiterId = jwt.getSubject();
        log.info("REST request to get jobs for recruiter: {}", recruiterId);

        List<JobOfferDTO> jobs = jobOfferService.getJobsByRecruiterId(recruiterId);
        return ResponseEntity.ok(jobs);
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