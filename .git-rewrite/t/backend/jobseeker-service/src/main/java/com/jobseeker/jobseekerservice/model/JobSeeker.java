package com.jobseeker.jobseekerservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeeker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;
    private String password;
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

    @ElementCollection
    private List<String> skills;

    @ElementCollection
    private List<String> experience;

    @ElementCollection
    private List<String> education;

    @Column(nullable = false)
    private boolean deleted = false;
}