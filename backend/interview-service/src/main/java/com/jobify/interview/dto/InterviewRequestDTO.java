package com.jobify.interview.dto;

import com.jobify.interview.entity.InterviewStatus;
import com.jobify.interview.entity.InterviewType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequestDTO {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotBlank(message = "Job seeker ID is required")
    private String jobSeekerId;

    @NotBlank(message = "Recruiter ID is required")
    private String recruiterId;

    @NotNull(message = "Scheduled date is required")
    @Future(message = "Scheduled date must be in the future")
    private LocalDateTime scheduledDate;

    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 480, message = "Duration cannot exceed 8 hours")
    private Integer duration;

    private String location;

    @NotNull(message = "Interview type is required")
    private InterviewType interviewType;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    private String meetingLink;
}