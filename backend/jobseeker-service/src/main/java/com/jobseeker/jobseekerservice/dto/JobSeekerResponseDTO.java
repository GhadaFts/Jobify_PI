package com.jobseeker.jobseekerservice.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class JobSeekerResponseDTO {
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
    private String title;
    private LocalDate dateOfBirth;
    private String gender;
    private List<String> skills;
    private List<String> experience;
    private List<String> education;
    private boolean deleted;
}
