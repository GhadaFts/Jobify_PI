package com.joboffer.jobofferservice.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class JobOfferResponseDTO {
    private Integer id;
    private String title;
    private String jobPosition;
    private String experience;
    private String salary;
    private String description;
    private String type;
    private Date createdAt;
    private String status;
    private boolean published;
    private List<String> requirements;
    private List<String> skills;
    private Double recruiterId;
}
