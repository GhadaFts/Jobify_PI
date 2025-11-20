package com.application.applicationservice.dto;

import com.application.applicationservice.model.ApplicationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequestDTO {
    private Integer jobSeekerId;
    private Integer jobOfferId;
    private String cvLink;
    private String motivationLettre;
    private ApplicationStatus status;
    
    private Double aiScore;
    private Boolean isFavorite;
}