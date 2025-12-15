package com.jobify.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetailsDTO {
    private String id;  // ✅ Changed from Long to String
    private LocalDateTime applicationDate;
    private String status;  // ✅ Will be the enum name as String
    private String cvLink;
    private String motivationLettre;
    private String jobSeekerId;
    private Double jobOfferId;  // ✅ Changed from jobId
    private Double aiScore;
    private Boolean isFavorite;
    private LocalDateTime lastStatusChange;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}