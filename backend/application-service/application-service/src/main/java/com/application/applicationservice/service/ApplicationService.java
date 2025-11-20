package com.application.applicationservice.service;

import com.application.applicationservice.client.JobOfferFeignClient;
import com.application.applicationservice.client.UserFeignClient;
import com.application.applicationservice.dto.ApplicationRequestDTO;
import com.application.applicationservice.dto.ApplicationResponseDTO;
import com.application.applicationservice.exception.ResourceNotFoundException;
import com.application.applicationservice.model.Application;
import com.application.applicationservice.model.ApplicationStatus;
import com.application.applicationservice.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository repository;
    private final UserFeignClient userFeignClient;
    private final JobOfferFeignClient jobOfferFeignClient;

    public ApplicationResponseDTO create(ApplicationRequestDTO dto) {
        // Validate JobSeeker exists (comment out for standalone testing)
        try {
            Boolean userExists = userFeignClient.validateUserExists(dto.getJobSeekerId());
            if (userExists == null || !userExists) {
                throw new ResourceNotFoundException("JobSeeker not found with ID: " + dto.getJobSeekerId());
            }
        } catch (Exception e) {
            // Comment this out for standalone testing without other services
            throw new ResourceNotFoundException("JobSeeker service unavailable or JobSeeker not found with ID: " + dto.getJobSeekerId());
        }

        // Validate JobOffer exists (comment out for standalone testing)
        try {
            Boolean jobExists = jobOfferFeignClient.validateJobExists(dto.getJobOfferId());
            if (jobExists == null || !jobExists) {
                throw new ResourceNotFoundException("JobOffer not found with ID: " + dto.getJobOfferId());
            }
        } catch (Exception e) {
            // Comment this out for standalone testing without other services
            throw new ResourceNotFoundException("JobOffer service unavailable or JobOffer not found with ID: " + dto.getJobOfferId());
        }

        // Check for duplicate application
        if (repository.existsByJobOfferIdAndJobSeekerId(dto.getJobOfferId(), dto.getJobSeekerId())) {
            throw new IllegalStateException("Application already exists for this JobOffer and JobSeeker");
        }

        Application application = Application.builder()
                .jobSeekerId(dto.getJobSeekerId())
                .jobOfferId(dto.getJobOfferId())
                .cvLink(dto.getCvLink())
                .motivationLettre(dto.getMotivationLettre())
                .status(dto.getStatus() != null ? dto.getStatus() : ApplicationStatus.NEW)
                .applicationDate(LocalDateTime.now())
                .isFavorite(dto.getIsFavorite() != null ? dto.getIsFavorite() : false)
                .build();  // ✅ REMOVED .createdAt() and .updatedAt() - MongoDB handles these
        
        Application saved = repository.save(application);
        return mapToDTO(saved);
    }

    public List<ApplicationResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ApplicationResponseDTO getById(String id) {
        Application application = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));
        return mapToDTO(application);
    }

    public List<ApplicationResponseDTO> getByJobOfferId(Integer jobOfferId) {
        return repository.findByJobOfferId(jobOfferId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponseDTO> getByJobSeekerId(Integer jobSeekerId) {
        return repository.findByJobSeekerId(jobSeekerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponseDTO updatePartial(String id, ApplicationRequestDTO dto) {
        Application application = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));

        if (dto.getStatus() != null) {
            application.setStatus(dto.getStatus());
            application.setLastStatusChange(LocalDateTime.now());
        }
        if (dto.getCvLink() != null) application.setCvLink(dto.getCvLink());
        if (dto.getMotivationLettre() != null) application.setMotivationLettre(dto.getMotivationLettre());
        if (dto.getAiScore() != null) application.setAiScore(dto.getAiScore());
        if (dto.getIsFavorite() != null) application.setIsFavorite(dto.getIsFavorite());
        
        // ✅ REMOVED application.setUpdatedAt() - MongoDB @LastModifiedDate handles this automatically

        Application updated = repository.save(application);
        return mapToDTO(updated);
    }

    @Transactional
    public ApplicationResponseDTO updateStatus(String id, ApplicationStatus status) {
        Application application = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));
        
        application.setStatus(status);
        application.setLastStatusChange(LocalDateTime.now());
        
        // ✅ REMOVED application.setUpdatedAt() - MongoDB @LastModifiedDate handles this automatically
        
        Application updated = repository.save(application);
        return mapToDTO(updated);
    }

    @Transactional
    public ApplicationResponseDTO updateAiScore(String id, Double aiScore) {
        Application application = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));
        
        application.setAiScore(aiScore);
        
        // ✅ REMOVED application.setUpdatedAt() - MongoDB @LastModifiedDate handles this automatically
        
        Application updated = repository.save(application);
        return mapToDTO(updated);
    }

    public void delete(String id) {
        Application application = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));
        repository.delete(application);
    }
    
    public Boolean checkDuplicateApplication(Integer jobOfferId, Integer jobSeekerId) {
        return repository.existsByJobOfferIdAndJobSeekerId(jobOfferId, jobSeekerId);
    }

    private ApplicationResponseDTO mapToDTO(Application application) {
        return ApplicationResponseDTO.builder()
                .id(application.getId())
                .applicationDate(application.getApplicationDate())
                .status(application.getStatus())
                .cvLink(application.getCvLink())
                .motivationLettre(application.getMotivationLettre())
                .jobSeekerId(application.getJobSeekerId())
                .jobOfferId(application.getJobOfferId())
                .aiScore(application.getAiScore())
                .isFavorite(application.getIsFavorite())
                .lastStatusChange(application.getLastStatusChange())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}