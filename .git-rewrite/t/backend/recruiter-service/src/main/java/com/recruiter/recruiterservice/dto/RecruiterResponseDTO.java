package com.recruiter.recruiterservice.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class RecruiterResponseDTO {
    private Integer id;
    private String email;
    private String fullName;
    private String role;
    private String photoProfil;
    private String twitterLink;
    private String webLink;
    private String githubLink;
    private String facebookLink;
    private String description;
    private String phoneNumber;
    private String nationality;

    private String companyAddress;
    private String domaine;
    private Integer employeesNumber;
    private List<String> service;

    private boolean deleted;
}
