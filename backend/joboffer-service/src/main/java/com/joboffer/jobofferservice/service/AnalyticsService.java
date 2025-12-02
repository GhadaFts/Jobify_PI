package com.joboffer.jobofferservice.service;

import com.joboffer.jobofferservice.model.JobOffer;
import com.joboffer.jobofferservice.repository.JobOfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final JobOfferRepository jobOfferRepository;

    /**
     * Get KPI data: total jobs, jobs last 7 days, etc.
     */
    public Map<String, Object> getKpiData() {
        log.info("Calculating KPI data");

        // Total jobs (all time)
        long totalJobs = jobOfferRepository.count();

        // Jobs posted in last 7 days
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long jobsLast7Days = jobOfferRepository.countByCreatedAtAfter(sevenDaysAgo);

        Map<String, Object> kpiData = new HashMap<>();
        kpiData.put("totalJobs", totalJobs);
        kpiData.put("jobsLast7Days", jobsLast7Days);
        kpiData.put("totalUsers", 0); // Will be fetched from auth-service
        kpiData.put("newUsersLast7Days", 0); // Will be fetched from auth-service
        kpiData.put("totalApplications", 0); // Will be fetched from application-service
        kpiData.put("avgApplicationsPerJob", 0.0);

        log.info("KPI data calculated: totalJobs={}, jobsLast7Days={}", totalJobs, jobsLast7Days);

        return kpiData;
    }

    /**
     * Get jobs posted over time (date range)
     */
    public Map<String, Object> getJobsOverTime(String startDateStr, String endDateStr) {
        log.info("Getting jobs over time from {} to {}", startDateStr, endDateStr);

        LocalDate startDate = startDateStr != null ? 
            LocalDate.parse(startDateStr) : LocalDate.now().minusDays(30);
        LocalDate endDate = endDateStr != null ? 
            LocalDate.parse(endDateStr) : LocalDate.now();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Get all jobs in date range
        var jobs = jobOfferRepository.findByCreatedAtBetween(startDateTime, endDateTime);

        // Group by date and count
        Map<LocalDate, Long> jobsByDate = jobs.stream()
            .collect(Collectors.groupingBy(
                job -> job.getCreatedAt().toLocalDate(),
                Collectors.counting()
            ));

        // Convert to list format
        List<Map<String, Object>> data = jobsByDate.entrySet().stream()
            .map(entry -> {
                Map<String, Object> point = new HashMap<>();
                point.put("date", entry.getKey().toString());
                point.put("count", entry.getValue());
                return point;
            })
            .sorted(Comparator.comparing(m -> (String) m.get("date")))
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now().toString());

        return response;
    }

    /**
     * Get top companies by job count and return recruiter IDs
     * Returns: company name, job count, recruiter ID
     */
    public Map<String, Object> getTopCompaniesByActivity() {
        log.info("Getting top companies by activity");

        // Get all jobs
        var allJobs = jobOfferRepository.findAll();

        // Group by company and collect recruiter IDs and job IDs
        Map<String, Map<String, Object>> companyData = new HashMap<>();
        
        for (var job : allJobs) {
            String company = job.getCompany();
            if (company == null || company.trim().isEmpty()) {
                company = "Unknown Company";
            }
            
            companyData.putIfAbsent(company, new HashMap<>());
            Map<String, Object> data = companyData.get(company);
            
            // Add recruiter ID (we'll use first one found for this company)
            if (!data.containsKey("recruiterId")) {
                data.put("recruiterId", job.getRecruiterId());
            }
            
            // Collect job IDs
            @SuppressWarnings("unchecked")
            List<Long> jobIds = (List<Long>) data.getOrDefault("jobIds", new ArrayList<>());
            jobIds.add(job.getId());
            data.put("jobIds", jobIds);
            
            // Count jobs
            data.put("jobCount", jobIds.size());
        }

        // Convert to list and sort by job count
        List<Map<String, Object>> companies = companyData.entrySet().stream()
            .map(entry -> {
                Map<String, Object> company = new HashMap<>();
                company.put("company", entry.getKey());
                company.put("jobCount", entry.getValue().get("jobCount"));
                company.put("recruiterId", entry.getValue().get("recruiterId"));
                company.put("jobIds", entry.getValue().get("jobIds"));
                return company;
            })
            .sorted((a, b) -> Integer.compare((Integer) b.get("jobCount"), (Integer) a.get("jobCount")))
            .limit(10)
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", companies);

        log.info("Top companies calculated: {} companies", companies.size());
        return response;
    }

    /**
     * Get top job categories (grouped by job position)
     */
    public Map<String, Object> getTopJobCategories() {
        log.info("Calculating top job categories by job position");

        List<JobOffer> allJobs = jobOfferRepository.findAll();
        
        // Group jobs by jobPosition
        var jobsByPosition = allJobs.stream()
            .filter(job -> job.getJobPosition() != null && !job.getJobPosition().trim().isEmpty())
            .collect(java.util.stream.Collectors.groupingBy(JobOffer::getJobPosition));

        // Create category data with job counts and jobIds
        var categoryData = jobsByPosition.entrySet().stream()
            .map(entry -> {
                String category = entry.getKey();
                List<JobOffer> jobs = entry.getValue();
                
                // Collect all job IDs for this category
                List<Long> jobIds = jobs.stream()
                    .map(JobOffer::getId)
                    .collect(java.util.stream.Collectors.toList());
                
                Map<String, Object> categoryInfo = new HashMap<>();
                categoryInfo.put("category", category);
                categoryInfo.put("jobCount", jobs.size());
                categoryInfo.put("jobIds", jobIds);
                
                return categoryInfo;
            })
            .sorted((a, b) -> Integer.compare(
                (Integer) b.get("jobCount"), 
                (Integer) a.get("jobCount")
            ))
            .limit(10) // Top 10 categories
            .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", categoryData);

        log.info("Top job categories calculated: {} categories", categoryData.size());
        return response;
    }

    /**
     * Get geographic distribution (jobs grouped by location with application counts)
     */
    public Map<String, Object> getGeographicDistribution() {
        log.info("Calculating geographic distribution by location");

        List<JobOffer> allJobs = jobOfferRepository.findAll();
        
        // Group jobs by location
        var jobsByLocation = allJobs.stream()
            .filter(job -> job.getLocation() != null && !job.getLocation().trim().isEmpty())
            .collect(java.util.stream.Collectors.groupingBy(JobOffer::getLocation));

        // Create location data with job counts and jobIds
        var locationData = jobsByLocation.entrySet().stream()
            .map(entry -> {
                String location = entry.getKey();
                List<JobOffer> jobs = entry.getValue();
                
                // Collect all job IDs for this location
                List<Long> jobIds = jobs.stream()
                    .map(JobOffer::getId)
                    .collect(java.util.stream.Collectors.toList());
                
                Map<String, Object> locationInfo = new HashMap<>();
                locationInfo.put("location", location);
                locationInfo.put("jobCount", jobs.size());
                locationInfo.put("jobIds", jobIds);
                
                return locationInfo;
            })
            .sorted((a, b) -> Integer.compare(
                (Integer) b.get("jobCount"), 
                (Integer) a.get("jobCount")
            ))
            .limit(10) // Top 10 locations
            .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", locationData);

        log.info("Geographic distribution calculated: {} locations", locationData.size());
        return response;
    }

    /**
     * Get jobs count for a specific date range
     */
    private long getJobsCountForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return jobOfferRepository.findAll().stream()
            .filter(job -> job.getCreatedAt() != null)
            .filter(job -> !job.getCreatedAt().isBefore(startDate) && !job.getCreatedAt().isAfter(endDate))
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
     * Get job posts alert (current period vs previous period)
     */
    public Map<String, Object> getJobPostsAlert(int days) {
        log.info("Calculating job posts alert for {} days", days);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentPeriodStart = now.minusDays(days);
        LocalDateTime previousPeriodStart = now.minusDays(days * 2);
        LocalDateTime previousPeriodEnd = currentPeriodStart;

        long currentPeriodCount = getJobsCountForPeriod(currentPeriodStart, now);
        long previousPeriodCount = getJobsCountForPeriod(previousPeriodStart, previousPeriodEnd);
        
        double percentageChange = calculatePercentageChange(currentPeriodCount, previousPeriodCount);
        
        Map<String, Object> response = new HashMap<>();
        response.put("current", currentPeriodCount);
        response.put("previous", previousPeriodCount);
        response.put("percentageChange", Math.round(percentageChange * 10.0) / 10.0);
        response.put("isAlert", percentageChange < -5.0); // Alert if decrease > 5%

        log.info("Job posts alert: current={}, previous={}, change={}%", 
                currentPeriodCount, previousPeriodCount, percentageChange);

        return response;
    }
}
