package com.application.applicationservice.client;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobOfferDTO {
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
    private Integer recruiterId;
}