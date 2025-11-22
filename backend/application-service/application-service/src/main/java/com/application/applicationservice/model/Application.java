package com.application.applicationservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "applications")
@CompoundIndex(name = "jobOffer_jobSeeker_idx", def = "{'jobOfferId': 1, 'jobSeekerId': 1}", unique = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    private String id;

    private LocalDateTime applicationDate;

    @Indexed
    private ApplicationStatus status;

    private String cvLink;

    private String motivationLettre;

    // Foreign Keys (references to other microservices)
    @Indexed
    private Integer jobSeekerId;

    @Indexed
    private Integer jobOfferId;

    // AI Ranking (score from AI Service - Gemini)
    private Double aiScore;

    // Favorite marker
    private Boolean isFavorite = false;

    // Audit fields
    private LocalDateTime lastStatusChange;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}