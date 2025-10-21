package com.recruiter.recruiterservice.service;


import com.recruiter.recruiterservice.dto.RecruiterRequestDTO;
import com.recruiter.recruiterservice.dto.RecruiterResponseDTO;
import com.recruiter.recruiterservice.exception.ResourceNotFoundException;
import com.recruiter.recruiterservice.model.Recruiter;
import com.recruiter.recruiterservice.repository.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruiterService {

    private final RecruiterRepository repository;

    public List<RecruiterResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RecruiterResponseDTO getById(Integer id) {
        Recruiter recruiter = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with id: " + id));
        return toDto(recruiter);
    }

    public RecruiterResponseDTO create(RecruiterRequestDTO dto) {
        Recruiter recruiter = Recruiter.builder()
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
                .companyAddress(dto.getCompanyAddress())
                .domaine(dto.getDomaine())
                .employeesNumber(dto.getEmployeesNumber())
                .service(dto.getService())
                .deleted(false)
                .build();
        repository.save(recruiter);
        return toDto(recruiter);
    }

    public RecruiterResponseDTO updatePartial(Integer id, RecruiterRequestDTO dto) {
        Recruiter recruiter = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with id: " + id));

        if (dto.getEmail() != null) recruiter.setEmail(dto.getEmail());
        if (dto.getPassword() != null) recruiter.setPassword(dto.getPassword());
        if (dto.getFullName() != null) recruiter.setFullName(dto.getFullName());
        if (dto.getRole() != null) recruiter.setRole(dto.getRole());
        if (dto.getPhotoProfil() != null) recruiter.setPhotoProfil(dto.getPhotoProfil());
        if (dto.getTwitterLink() != null) recruiter.setTwitterLink(dto.getTwitterLink());
        if (dto.getWebLink() != null) recruiter.setWebLink(dto.getWebLink());
        if (dto.getGithubLink() != null) recruiter.setGithubLink(dto.getGithubLink());
        if (dto.getFacebookLink() != null) recruiter.setFacebookLink(dto.getFacebookLink());
        if (dto.getDescription() != null) recruiter.setDescription(dto.getDescription());
        if (dto.getPhoneNumber() != null) recruiter.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getNationality() != null) recruiter.setNationality(dto.getNationality());
        if (dto.getCompanyAddress() != null) recruiter.setCompanyAddress(dto.getCompanyAddress());
        if (dto.getDomaine() != null) recruiter.setDomaine(dto.getDomaine());
        if (dto.getEmployeesNumber() != null) recruiter.setEmployeesNumber(dto.getEmployeesNumber());
        if (dto.getService() != null) recruiter.setService(dto.getService());

        repository.save(recruiter);
        return toDto(recruiter);
    }

    public void delete(Integer id) {
        Recruiter recruiter = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with id: " + id));
        recruiter.setDeleted(true); // soft delete
        repository.save(recruiter);
    }

    private RecruiterResponseDTO toDto(Recruiter recruiter) {
        return RecruiterResponseDTO.builder()
                .id(recruiter.getId())
                .email(recruiter.getEmail())
                .fullName(recruiter.getFullName())
                .role(recruiter.getRole())
                .photoProfil(recruiter.getPhotoProfil())
                .twitterLink(recruiter.getTwitterLink())
                .webLink(recruiter.getWebLink())
                .githubLink(recruiter.getGithubLink())
                .facebookLink(recruiter.getFacebookLink())
                .description(recruiter.getDescription())
                .phoneNumber(recruiter.getPhoneNumber())
                .nationality(recruiter.getNationality())
                .companyAddress(recruiter.getCompanyAddress())
                .domaine(recruiter.getDomaine())
                .employeesNumber(recruiter.getEmployeesNumber())
                .service(recruiter.getService())
                .deleted(recruiter.isDeleted())
                .build();
    }
}
