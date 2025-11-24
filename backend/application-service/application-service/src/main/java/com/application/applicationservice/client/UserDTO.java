package com.application.applicationservice.client;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
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