package com.application.applicationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {
    private String id;
    private String jobSeekerId;
    private Long jobOfferId;
    private LocalDateTime createdAt;
}
