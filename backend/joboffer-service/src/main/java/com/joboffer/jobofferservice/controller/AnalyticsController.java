package com.joboffer.jobofferservice.controller;

import com.joboffer.jobofferservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Get KPI data for admin dashboard
     * GET /api/analytics/kpi
     */
    @GetMapping("/kpi")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getKpiData() {
        log.info("REST request to get KPI data");
        
        Map<String, Object> kpiData = analyticsService.getKpiData();
        return ResponseEntity.ok(kpiData);
    }

    /**
     * Get jobs count by date range
     * GET /api/analytics/jobs-over-time
     */
    @GetMapping("/jobs-over-time")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getJobsOverTime(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        log.info("REST request to get jobs over time - startDate: {}, endDate: {}", startDate, endDate);
        
        Map<String, Object> data = analyticsService.getJobsOverTime(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    /**
     * Get top companies by activity
     * GET /api/analytics/top-companies
     */
    @GetMapping("/top-companies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getTopCompanies() {
        log.info("REST request to get top companies");
        Map<String, Object> data = analyticsService.getTopCompaniesByActivity();
        return ResponseEntity.ok(data);
    }

    /**
     * Get top job categories
     * GET /api/analytics/top-categories
     */
    @GetMapping("/top-categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getTopCategories() {
        log.info("REST request to get top job categories");
        Map<String, Object> data = analyticsService.getTopJobCategories();
        return ResponseEntity.ok(data);
    }

    /**
     * Get geographic distribution
     * GET /api/analytics/geographic-distribution
     */
    @GetMapping("/geographic-distribution")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getGeographicDistribution() {
        log.info("REST request to get geographic distribution");
        Map<String, Object> data = analyticsService.getGeographicDistribution();
        return ResponseEntity.ok(data);
    }

    /**
     * Get job posts metric for alerts (current period vs previous period)
     * GET /api/analytics/job-posts-alert
     */
    @GetMapping("/job-posts-alert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getJobPostsAlert(
            @RequestParam(required = false, defaultValue = "7") int days) {
        log.info("REST request to get job posts alert for {} days", days);
        Map<String, Object> data = analyticsService.getJobPostsAlert(days);
        return ResponseEntity.ok(data);
    }
}
