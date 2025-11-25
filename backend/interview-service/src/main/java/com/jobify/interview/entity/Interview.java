package com.jobify.interview.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long applicationId;

    @Column(nullable = false)
    private String jobSeekerId;

    @Column(nullable = false)
    private String recruiterId;

    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    @Column(nullable = false)
    private Integer duration; // in minutes

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewType interviewType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status;

    @Column(length = 1000)
    private String notes;

    private String meetingLink;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = InterviewStatus.SCHEDULED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean isUpcoming() {
        return scheduledDate.isAfter(LocalDateTime.now()) &&
                status == InterviewStatus.SCHEDULED;
    }

    public boolean canBeRescheduled() {
        return status == InterviewStatus.SCHEDULED ||
                status == InterviewStatus.RESCHEDULED;
    }

    public boolean shouldSendReminder() {
        if (!isUpcoming()) return false;

        LocalDateTime reminderTime = scheduledDate.minusHours(24);
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(reminderTime) && now.isBefore(scheduledDate);
    }
}