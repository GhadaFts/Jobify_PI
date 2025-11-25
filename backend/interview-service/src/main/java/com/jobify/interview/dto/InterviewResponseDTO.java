package com.jobify.interview.dto;

import com.jobify.interview.entity.InterviewStatus;
import com.jobify.interview.entity.InterviewType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponseDTO {

    private Long id;
    private Long applicationId;
    private String jobSeekerId;
    private String recruiterId;
    private LocalDateTime scheduledDate;
    private Integer duration;
    private String location;
    private InterviewType interviewType;
    private InterviewStatus status;
    private String notes;
    private String meetingLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional info from other services (optional)
    private ApplicationDetailsDTO applicationDetails;
    private UserDetailsDTO jobSeekerDetails;
    private UserDetailsDTO recruiterDetails;
}