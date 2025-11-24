package com.joboffer.jobofferservice.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class JobOfferRequestDTO {
    private String title;
    private String jobPosition;
    private String experience;
    private String salary;
    private String description;
    private String type;
    private List<String> requirements;
    private List<String> skills;
    private boolean published;
    private String status;
    private Double recruiterId; // foreign key reference
}
