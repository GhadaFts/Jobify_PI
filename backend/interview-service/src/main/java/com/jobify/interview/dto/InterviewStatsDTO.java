package com.jobify.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewStatsDTO {
    private Long totalInterviews;
    private Long scheduledInterviews;
    private Long completedInterviews;
    private Long cancelledInterviews;
    private Long upcomingInterviews;
}