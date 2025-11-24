package com.jobify.interview.dto;

import com.jobify.interview.entity.InterviewStatus;
import com.jobify.interview.entity.InterviewType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewUpdateDTO {

    @Future(message = "Scheduled date must be in the future")
    private LocalDateTime scheduledDate;

    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 480, message = "Duration cannot exceed 8 hours")
    private Integer duration;

    private String location;

    private InterviewType interviewType;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    private String meetingLink;

    private InterviewStatus status;
}
