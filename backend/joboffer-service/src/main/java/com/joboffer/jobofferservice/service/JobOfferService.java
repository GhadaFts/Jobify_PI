package com.joboffer.jobofferservice.service;

import com.joboffer.jobofferservice.dto.JobOfferDTO;
import com.joboffer.jobofferservice.exception.ResourceNotFoundException;
import com.joboffer.jobofferservice.model.JobOffer;
import com.joboffer.jobofferservice.repository.JobOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobOfferService {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    /**
     * Search job offers with filters
     */
    public List<JobOfferDTO> searchJobs(Specification<JobOffer> filters) {
        List<JobOffer> jobOffers = jobOfferRepository.findAll(filters);
        return jobOffers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get job offer by ID
     */
    public JobOfferDTO getJobById(Long id) {
        JobOffer jobOffer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job offer not found with id: " + id));
        return convertToDTO(jobOffer);
    }

    /**
     * Create new job offer
     */
    public JobOfferDTO createJob(JobOfferDTO jobOfferDTO) {
        JobOffer jobOffer = convertToEntity(jobOfferDTO);
        jobOffer.setCreatedAt(LocalDateTime.now());
        jobOffer.setUpdatedAt(LocalDateTime.now());
        
        if (jobOffer.getStatus() == null || jobOffer.getStatus().isEmpty()) {
            jobOffer.setStatus("OPEN");
        }
        
        JobOffer savedJob = jobOfferRepository.save(jobOffer);
        return convertToDTO(savedJob);
    }

    /**
     * Update existing job offer
     */
    public JobOfferDTO updateJob(Long id, JobOfferDTO data) {
        JobOffer existingJob = jobOfferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job offer not found with id: " + id));

        if (data.getTitle() != null) existingJob.setTitle(data.getTitle());
        if (data.getJobPosition() != null) existingJob.setJobPosition(data.getJobPosition());
        if (data.getExperience() != null) existingJob.setExperience(data.getExperience());
        if (data.getSalary() != null) existingJob.setSalary(data.getSalary());
        if (data.getRequirements() != null) existingJob.setRequirements(data.getRequirements());
        if (data.getLocation() != null) existingJob.setLocation(data.getLocation());
        if (data.getCompany() != null) existingJob.setCompany(data.getCompany());
        if (data.getCurrency() != null) existingJob.setCurrency(data.getCurrency());
        if (data.getType() != null) existingJob.setType(data.getType());
        if (data.getSkills() != null) existingJob.setSkills(data.getSkills());
        if (data.getDescription() != null) existingJob.setDescription(data.getDescription());
        if (data.getRecruiterEmail() != null) existingJob.setRecruiterEmail(data.getRecruiterEmail());
        if (data.getApplicationDeadline() != null) existingJob.setApplicationDeadline(data.getApplicationDeadline());
        if (data.getStatus() != null) existingJob.setStatus(data.getStatus());
        
        existingJob.setPublished(data.isPublished());
        existingJob.setUpdatedAt(LocalDateTime.now());

        JobOffer updatedJob = jobOfferRepository.save(existingJob);
        return convertToDTO(updatedJob);
    }

    /**
     * Delete job offer by ID
     */
    public void deleteJob(Long id) {
        if (!jobOfferRepository.existsById(id)) {
            throw new ResourceNotFoundException("Job offer not found with id: " + id);
        }
        jobOfferRepository.deleteById(id);
    }

    /**
     * Validate job offer data
     */
    public boolean validateJobData(JobOfferDTO data) {
        if (data.getTitle() == null || data.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Job title is required");
        }
        if (data.getJobPosition() == null || data.getJobPosition().trim().isEmpty()) {
            throw new IllegalArgumentException("Job position is required");
        }
        if (data.getCompany() == null || data.getCompany().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required");
        }
        if (data.getLocation() == null || data.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }
        return true;
    }

    /**
     * Convert JobOffer entity to DTO
     */
    private JobOfferDTO convertToDTO(JobOffer jobOffer) {
        JobOfferDTO dto = new JobOfferDTO();
        dto.setId(jobOffer.getId());
        dto.setTitle(jobOffer.getTitle());
        dto.setJobPosition(jobOffer.getJobPosition());
        dto.setExperience(jobOffer.getExperience());
        dto.setSalary(jobOffer.getSalary());
        dto.setRequirements(jobOffer.getRequirements());
        dto.setLocation(jobOffer.getLocation());
        dto.setCompany(jobOffer.getCompany());
        dto.setCurrency(jobOffer.getCurrency());
        dto.setType(jobOffer.getType());
        dto.setSkills(jobOffer.getSkills());
        dto.setRecruiterEmail(jobOffer.getRecruiterEmail());
        dto.setApplicationDeadline(jobOffer.getApplicationDeadline());
        dto.setCreatedAt(jobOffer.getCreatedAt());
        dto.setUpdatedAt(jobOffer.getUpdatedAt());
        dto.setDescription(jobOffer.getDescription());
        dto.setPublished(jobOffer.isPublished());
        dto.setStatus(jobOffer.getStatus());
        return dto;
    }

    /**
     * Convert DTO to JobOffer entity
     */
    private JobOffer convertToEntity(JobOfferDTO dto) {
        JobOffer jobOffer = new JobOffer();
        if (dto.getId() != null) {
            jobOffer.setId(dto.getId());
        }
        jobOffer.setTitle(dto.getTitle());
        jobOffer.setJobPosition(dto.getJobPosition());
        jobOffer.setExperience(dto.getExperience());
        jobOffer.setSalary(dto.getSalary());
        jobOffer.setRequirements(dto.getRequirements());
        jobOffer.setLocation(dto.getLocation());
        jobOffer.setCompany(dto.getCompany());
        jobOffer.setCurrency(dto.getCurrency());
        jobOffer.setType(dto.getType());
        jobOffer.setSkills(dto.getSkills());
        jobOffer.setRecruiterEmail(dto.getRecruiterEmail());
        jobOffer.setApplicationDeadline(dto.getApplicationDeadline());
        jobOffer.setDescription(dto.getDescription());
        jobOffer.setPublished(dto.isPublished());
        jobOffer.setStatus(dto.getStatus());
        return jobOffer;
    }
}