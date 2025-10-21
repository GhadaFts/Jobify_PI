package com.jobseeker.jobseekerservice.service;

import com.jobseeker.jobseekerservice.dto.JobSeekerRequestDTO;
import com.jobseeker.jobseekerservice.dto.JobSeekerResponseDTO;
import com.jobseeker.jobseekerservice.exception.ResourceNotFoundException;
import com.jobseeker.jobseekerservice.model.JobSeeker;
import com.jobseeker.jobseekerservice.repository.JobSeekerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSeekerService {

    private final JobSeekerRepository repository;

    public List<JobSeekerResponseDTO> getAll() {
        return repository.findByDeletedFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public JobSeekerResponseDTO getById(Integer id) {
        JobSeeker jobSeeker = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker not found with ID: " + id));
        return mapToResponse(jobSeeker);
    }

    public JobSeekerResponseDTO create(JobSeekerRequestDTO dto) {
        JobSeeker jobSeeker = mapToEntity(dto);
        JobSeeker saved = repository.save(jobSeeker);
        return mapToResponse(saved);
    }

    public void delete(Integer id) {
        JobSeeker jobSeeker = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobSeeker not found with ID: " + id));
        jobSeeker.setDeleted(true);
        repository.save(jobSeeker);

    }

    private JobSeekerResponseDTO mapToResponse(JobSeeker entity) {
        return JobSeekerResponseDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .role(entity.getRole())
                .photoProfil(entity.getPhotoProfil())
                .twitterLink(entity.getTwitterLink())
                .webLink(entity.getWebLink())
                .githubLink(entity.getGithubLink())
                .facebookLink(entity.getFacebookLink())
                .description(entity.getDescription())
                .phoneNumber(entity.getPhoneNumber())
                .nationality(entity.getNationality())
                .title(entity.getTitle())
                .dateOfBirth(entity.getDateOfBirth())
                .gender(entity.getGender())
                .skills(entity.getSkills())
                .experience(entity.getExperience())
                .education(entity.getEducation())
                .deleted(entity.isDeleted())
                .build();
    }

    private JobSeeker mapToEntity(JobSeekerRequestDTO dto) {
        return JobSeeker.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .fullName(dto.getFullName())
                .role(dto.getRole())
                .photoProfil(dto.getPhotoProfil())
                .twitterLink(dto.getTwitterLink())
                .webLink(dto.getWebLink())
                .githubLink(dto.getGithubLink())
                .facebookLink(dto.getFacebookLink())
                .description(dto.getDescription())
                .phoneNumber(dto.getPhoneNumber())
                .nationality(dto.getNationality())
                .title(dto.getTitle())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .skills(dto.getSkills())
                .experience(dto.getExperience())
                .education(dto.getEducation())
                .build();
    }
}
