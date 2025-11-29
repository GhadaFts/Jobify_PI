package com.application.applicationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "bookmarks")
@CompoundIndex(name = "job_seeker_job_offer", def = "{'jobSeekerId': 1, 'jobOfferId': 1}", unique = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

    @Id
    private String id;

    private String jobSeekerId;  // Keycloak ID

    private Long jobOfferId;     // From JobOffer Service

    @CreatedDate
    private LocalDateTime createdAt;
}
