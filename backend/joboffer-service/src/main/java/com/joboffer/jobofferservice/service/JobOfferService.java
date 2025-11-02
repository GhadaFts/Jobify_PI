package com.joboffer.jobofferservice.service;

import com.joboffer.jobofferservice.dto.JobOfferRequestDTO;
import com.joboffer.jobofferservice.dto.JobOfferResponseDTO;
import com.joboffer.jobofferservice.exception.ResourceNotFoundException;
import com.joboffer.jobofferservice.model.JobOffer;
import com.joboffer.jobofferservice.repository.JobOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobOfferService {

    private final JobOfferRepository repository;

    public JobOfferResponseDTO create(JobOfferRequestDTO dto) {
        JobOffer jobOffer = JobOffer.builder()
                .title(dto.getTitle())
                .jobPosition(dto.getJobPosition())
                .experience(dto.getExperience())
                .salary(dto.getSalary())
                .description(dto.getDescription())
                .type(dto.getType())
                .requirements(dto.getRequirements())
                .skills(dto.getSkills())
                .published(dto.isPublished())
                .recruiterId(dto.getRecruiterId())
                .createdAt(new java.util.Date())
                .status(dto.getStatus())
                .build();
        repository.save(jobOffer);
        return mapToDTO(jobOffer);
    }

    public List<JobOfferResponseDTO> getAll() {
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public JobOfferResponseDTO getById(Integer id) {
        return repository.findById(id).map(this::mapToDTO).orElseThrow(() -> new ResourceNotFoundException("JobOffer not found"));
    }

    @Transactional
    public JobOfferResponseDTO updatePartial(Integer id, JobOfferRequestDTO dto) {
        JobOffer jobOffer = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("JobOffer not found"));

        if (dto.getTitle() != null) jobOffer.setTitle(dto.getTitle());
        if (dto.getJobPosition() != null) jobOffer.setJobPosition(dto.getJobPosition());
        if (dto.getExperience() != null) jobOffer.setExperience(dto.getExperience());
        if (dto.getSalary() != null) jobOffer.setSalary(dto.getSalary());
        if (dto.getDescription() != null) jobOffer.setDescription(dto.getDescription());
        if (dto.getType() != null) jobOffer.setType(dto.getType());
        if (dto.getRequirements() != null) jobOffer.setRequirements(dto.getRequirements());
        if (dto.getSkills() != null) jobOffer.setSkills(dto.getSkills());
        jobOffer.setPublished(dto.isPublished());
        if (dto.getRecruiterId() != null) jobOffer.setRecruiterId(dto.getRecruiterId());
        if(dto.getStatus()!=null) jobOffer.setStatus(dto.getStatus());

        return mapToDTO(jobOffer);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    private JobOfferResponseDTO mapToDTO(JobOffer jobOffer) {
        return JobOfferResponseDTO.builder()
                .id(jobOffer.getId())
                .title(jobOffer.getTitle())
                .jobPosition(jobOffer.getJobPosition())
                .experience(jobOffer.getExperience())
                .salary(jobOffer.getSalary())
                .description(jobOffer.getDescription())
                .type(jobOffer.getType())
                .requirements(jobOffer.getRequirements())
                .skills(jobOffer.getSkills())
                .published(jobOffer.isPublished())
                .recruiterId(jobOffer.getRecruiterId())
                .createdAt(jobOffer.getCreatedAt())
                .status(jobOffer.getStatus())
                .build();
    }
}
