package com.joboffer.jobofferservice.controller;

import com.joboffer.jobofferservice.dto.JobOfferDTO;
import com.joboffer.jobofferservice.model.JobOffer;
import com.joboffer.jobofferservice.service.JobOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobOfferController {

    @Autowired
    private JobOfferService jobOfferService;

    /**
     * Search jobs with filters
     * GET /api/jobs
     */
    @GetMapping
    public ResponseEntity<List<JobOfferDTO>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String location) {
        
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
     * Get job by ID
     * GET /api/jobs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobOfferDTO> getJobById(@PathVariable Long id) {
        JobOfferDTO job = jobOfferService.getJobById(id);
        return ResponseEntity.ok(job);
    }

    /**
     * Create new job
     * POST /api/jobs
     */
    @PostMapping
    public ResponseEntity<JobOfferDTO> createJob(@RequestBody JobOfferDTO jobOfferDTO) {
        jobOfferService.validateJobData(jobOfferDTO);
        JobOfferDTO createdJob = jobOfferService.createJob(jobOfferDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
    }

    /**
     * Update job
     * PUT /api/jobs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobOfferDTO> updateJob(
            @PathVariable Long id,
            @RequestBody JobOfferDTO jobOfferDTO) {
        JobOfferDTO updatedJob = jobOfferService.updateJob(id, jobOfferDTO);
        return ResponseEntity.ok(updatedJob);
    }

    /**
     * Delete job
     * DELETE /api/jobs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobOfferService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
}