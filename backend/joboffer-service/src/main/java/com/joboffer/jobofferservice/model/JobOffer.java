package com.joboffer.jobofferservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String jobPosition;
    private String experience;
    private String salary;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String type;
    private Date createdAt;
    private String status = "OPEN";
    private boolean published;

    @ElementCollection
    private List<String> requirements;

    @ElementCollection
    private List<String> skills;

    // Reference to recruiter in another service
    private Double recruiterId;
}
