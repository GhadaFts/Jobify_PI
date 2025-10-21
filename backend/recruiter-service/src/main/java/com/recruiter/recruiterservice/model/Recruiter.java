package com.recruiter.recruiterservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruiter {

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
    @Column(columnDefinition = "TEXT")
    private String description;
    private String phoneNumber;
    private String nationality;

    private String companyAddress;
    private String domaine;
    private Integer employeesNumber;

    @ElementCollection
    private List<String> service;

    private boolean deleted = false; // soft delete
}
