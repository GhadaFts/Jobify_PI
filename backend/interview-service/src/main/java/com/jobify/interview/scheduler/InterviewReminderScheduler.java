package com.jobify.interview.scheduler;

import com.jobify.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewReminderScheduler {

    private final InterviewService interviewService;

    /**
     * Automatically sends interview reminders every day at 9:00 AM
     * Cron format: second minute hour day month day-of-week
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyInterviewReminders() {
        log.info("=== SCHEDULED JOB: Sending interview reminders ===");
        interviewService.sendInterviewReminders();
        log.info("=== SCHEDULED JOB: Completed ===");
    }

    /**
     * Alternative: Send reminders every 6 hours
     */
    // @Scheduled(fixedRate = 21600000) // 6 hours in milliseconds
    public void sendPeriodicReminders() {
        log.info("=== PERIODIC REMINDER JOB STARTED ===");
        interviewService.sendInterviewReminders();
        log.info("=== PERIODIC REMINDER JOB COMPLETED ===");
    }

    /**
     * Alternative: Send reminders every hour
     */
    // @Scheduled(cron = "0 0 * * * *")
    public void sendHourlyReminders() {
        log.info("=== HOURLY REMINDER CHECK ===");
        interviewService.sendInterviewReminders();
    }
}