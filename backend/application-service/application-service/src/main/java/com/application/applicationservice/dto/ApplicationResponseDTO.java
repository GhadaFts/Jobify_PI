package com.application.applicationservice.dto;

import com.application.applicationservice.model.ApplicationStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponseDTO {
    private String id;
    private LocalDateTime applicationDate;
    private ApplicationStatus status;
    private String cvLink;
    private String motivationLettre;
    private Double jobSeekerId;
    private Double jobOfferId;
    private Double aiScore;
    private Boolean isFavorite;
    
    private LocalDateTime lastStatusChange;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}