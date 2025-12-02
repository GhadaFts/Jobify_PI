package com.application.applicationservice.service;

import com.application.applicationservice.model.ApplicationStatus;
import com.application.applicationservice.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final ApplicationRepository applicationRepository;

    /**
     * Get application statistics
     */
    public Map<String, Object> getApplicationStats() {
        log.info("Calculating application statistics");

        // Total applications (all time)
        long totalApplications = applicationRepository.count();

        // Calculate average applications per job
        double avgApplicationsPerJob = 0.0;
        
        // Get all applications and count distinct job offers
        var allApplications = applicationRepository.findAll();
        long distinctJobCount = allApplications.stream()
                .map(app -> app.getJobOfferId())
                .distinct()
                .count();
        
        if (distinctJobCount > 0) {
            avgApplicationsPerJob = (double) totalApplications / distinctJobCount;
            avgApplicationsPerJob = Math.round(avgApplicationsPerJob * 10.0) / 10.0; // Round to 1 decimal
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalApplications", totalApplications);
        stats.put("avgApplicationsPerJob", avgApplicationsPerJob);

        log.info("Application stats calculated: totalApplications={}, distinctJobCount={}, avgApplicationsPerJob={}", 
                 totalApplications, distinctJobCount, avgApplicationsPerJob);

        return stats;
    }

    /**
     * Get application funnel data
     */
    public Map<String, Object> getApplicationFunnel() {
        log.info("Calculating application funnel data");

        // Total applications submitted
        long totalApplications = applicationRepository.count();

        // Interviews scheduled (INTERVIEW_SCHEDULED status)
        long interviewsScheduled = applicationRepository.findByStatus(ApplicationStatus.INTERVIEW_SCHEDULED).size();

        // Offers accepted (ACCEPTED status)
        long offersAccepted = applicationRepository.findByStatus(ApplicationStatus.ACCEPTED).size();

        Map<String, Object> funnelData = new HashMap<>();
        funnelData.put("applications", totalApplications);
        funnelData.put("interviews", interviewsScheduled);
        funnelData.put("hires", offersAccepted);

        log.info("Funnel data calculated: applications={}, interviews={}, hires={}", 
                 totalApplications, interviewsScheduled, offersAccepted);

        return funnelData;
    }

    /**
     * Get applications per job data
     */
    public Map<String, Object> getApplicationsPerJob() {
        log.info("Calculating applications per job");

        var allApplications = applicationRepository.findAll();
        
        // Group applications by jobOfferId and count them
        var appsPerJob = allApplications.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                app -> app.getJobOfferId(),
                java.util.stream.Collectors.counting()
            ));

        // Convert to list of maps for JSON response
        var result = appsPerJob.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // Sort by count descending
            .limit(10) // Top 10 jobs
            .map(entry -> {
                Map<String, Object> jobData = new HashMap<>();
                jobData.put("jobId", entry.getKey().longValue());
                jobData.put("count", entry.getValue());
                return jobData;
            })
            .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", result);

        log.info("Applications per job calculated: {} job offers", result.size());

        return response;
    }

    /**
     * Get application counts for multiple job IDs
     */
    public Map<String, Object> getApplicationCountsByJobs(Map<String, Object> request) {
        log.info("Calculating application counts for job IDs");

        var jobIds = (java.util.List<?>) request.get("jobIds");
        
        if (jobIds == null || jobIds.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("counts", new HashMap<>());
            return response;
        }

        // Convert jobIds to Long list
        var jobIdLongs = jobIds.stream()
            .map(id -> {
                if (id instanceof Number) {
                    return ((Number) id).longValue();
                } else if (id instanceof String) {
                    try {
                        return Long.parseLong((String) id);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                return null;
            })
            .filter(id -> id != null)
            .collect(java.util.stream.Collectors.toList());

        // Get all applications
        var allApplications = applicationRepository.findAll();
        
        // Count applications for each job ID
        // Note: jobOfferId is stored as Double in MongoDB, so we need to compare as doubles
        Map<Long, Long> counts = new HashMap<>();
        for (Long jobId : jobIdLongs) {
            long count = allApplications.stream()
                .filter(app -> app.getJobOfferId() != null && jobId.doubleValue() == app.getJobOfferId())
                .count();
            counts.put(jobId, count);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("counts", counts);

        log.info("Application counts calculated for {} job IDs: {}", jobIdLongs.size(), counts);

        return response;
    }

    /**
     * Get applications count for a specific date range
     */
    private long getApplicationsCountForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return applicationRepository.findAll().stream()
            .filter(app -> app.getApplicationDate() != null)
            .filter(app -> !app.getApplicationDate().isBefore(startDate) && !app.getApplicationDate().isAfter(endDate))
            .count();
    }

    /**
     * Get interviews count for a specific date range
     */
    private long getInterviewsCountForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return applicationRepository.findAll().stream()
            .filter(app -> app.getStatus() == ApplicationStatus.INTERVIEW_SCHEDULED)
            .filter(app -> app.getLastStatusChange() != null)
            .filter(app -> !app.getLastStatusChange().isBefore(startDate) && !app.getLastStatusChange().isAfter(endDate))
            .count();
    }

    /**
     * Get hires count for a specific date range
     */
    private long getHiresCountForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return applicationRepository.findAll().stream()
            .filter(app -> app.getStatus() == ApplicationStatus.ACCEPTED)
            .filter(app -> app.getLastStatusChange() != null)
            .filter(app -> !app.getLastStatusChange().isBefore(startDate) && !app.getLastStatusChange().isAfter(endDate))
            .count();
    }

    /**
     * Calculate percentage change
     */
    private double calculatePercentageChange(long current, long previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((double) (current - previous) / previous) * 100;
    }

    /**
     * Get applications alert
     */
    public Map<String, Object> getApplicationsAlert(int days) {
        log.info("Calculating applications alert for {} days", days);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentPeriodStart = now.minusDays(days);
        LocalDateTime previousPeriodStart = now.minusDays(days * 2);
        LocalDateTime previousPeriodEnd = currentPeriodStart;

        long currentPeriodCount = getApplicationsCountForPeriod(currentPeriodStart, now);
        long previousPeriodCount = getApplicationsCountForPeriod(previousPeriodStart, previousPeriodEnd);
        
        double percentageChange = calculatePercentageChange(currentPeriodCount, previousPeriodCount);
        
        Map<String, Object> response = new HashMap<>();
        response.put("current", currentPeriodCount);
        response.put("previous", previousPeriodCount);
        response.put("percentageChange", Math.round(percentageChange * 10.0) / 10.0);
        response.put("isAlert", percentageChange < -5.0);

        log.info("Applications alert: current={}, previous={}, change={}%", 
                currentPeriodCount, previousPeriodCount, percentageChange);

        return response;
    }

    /**
     * Get interviews alert
     */
    public Map<String, Object> getInterviewsAlert(int days) {
        log.info("Calculating interviews alert for {} days", days);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentPeriodStart = now.minusDays(days);
        LocalDateTime previousPeriodStart = now.minusDays(days * 2);
        LocalDateTime previousPeriodEnd = currentPeriodStart;

        long currentPeriodCount = getInterviewsCountForPeriod(currentPeriodStart, now);
        long previousPeriodCount = getInterviewsCountForPeriod(previousPeriodStart, previousPeriodEnd);
        
        double percentageChange = calculatePercentageChange(currentPeriodCount, previousPeriodCount);
        
        Map<String, Object> response = new HashMap<>();
        response.put("current", currentPeriodCount);
        response.put("previous", previousPeriodCount);
        response.put("percentageChange", Math.round(percentageChange * 10.0) / 10.0);
        response.put("isAlert", percentageChange < -5.0);

        log.info("Interviews alert: current={}, previous={}, change={}%", 
                currentPeriodCount, previousPeriodCount, percentageChange);

        return response;
    }

    /**
     * Get hiring alert
     */
    public Map<String, Object> getHiringAlert(int days) {
        log.info("Calculating hiring alert for {} days", days);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentPeriodStart = now.minusDays(days);
        LocalDateTime previousPeriodStart = now.minusDays(days * 2);
        LocalDateTime previousPeriodEnd = currentPeriodStart;

        long currentPeriodHires = getHiresCountForPeriod(currentPeriodStart, now);
        long previousPeriodHires = getHiresCountForPeriod(previousPeriodStart, previousPeriodEnd);
        long currentPeriodApps = getApplicationsCountForPeriod(currentPeriodStart, now);
        long previousPeriodApps = getApplicationsCountForPeriod(previousPeriodStart, previousPeriodEnd);

        double currentRate = currentPeriodApps > 0 ? ((double) currentPeriodHires / currentPeriodApps) * 100 : 0;
        double previousRate = previousPeriodApps > 0 ? ((double) previousPeriodHires / previousPeriodApps) * 100 : 0;
        double percentageChange = previousRate > 0 ? ((currentRate - previousRate) / previousRate) * 100 : 0;
        
        Map<String, Object> response = new HashMap<>();
        response.put("currentRate", Math.round(currentRate * 10.0) / 10.0);
        response.put("previousRate", Math.round(previousRate * 10.0) / 10.0);
        response.put("percentageChange", Math.round(percentageChange * 10.0) / 10.0);
        response.put("isAlert", percentageChange < -5.0);

        log.info("Hiring rate alert: current={}%, previous={}%, change={}%", 
                currentRate, previousRate, percentageChange);

        return response;
    }
}
