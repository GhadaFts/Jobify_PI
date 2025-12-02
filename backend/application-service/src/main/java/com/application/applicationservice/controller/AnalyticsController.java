package com.application.applicationservice.controller;

import com.application.applicationservice.service.AnalyticsService;
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
     * Get application statistics
     * GET /api/analytics/applications
     */
    @GetMapping("/applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getApplicationStats() {
        log.info("REST request to get application statistics");
        
        Map<String, Object> stats = analyticsService.getApplicationStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get application funnel data
     * GET /api/analytics/funnel
     */
    @GetMapping("/funnel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getApplicationFunnel() {
        log.info("REST request to get application funnel data");
        
        Map<String, Object> funnelData = analyticsService.getApplicationFunnel();
        return ResponseEntity.ok(funnelData);
    }

    /**
     * Get applications per job data
     * GET /api/analytics/apps-per-job
     */
    @GetMapping("/apps-per-job")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getApplicationsPerJob() {
        log.info("REST request to get applications per job data");
        
        Map<String, Object> appsPerJob = analyticsService.getApplicationsPerJob();
        return ResponseEntity.ok(appsPerJob);
    }

    /**
     * Get application counts for multiple job IDs
     * POST /api/analytics/count-by-jobs
     */
    @PostMapping("/count-by-jobs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getApplicationCountsByJobs(@RequestBody Map<String, Object> request) {
        log.info("REST request to get application counts by job IDs");
        
        Map<String, Object> counts = analyticsService.getApplicationCountsByJobs(request);
        return ResponseEntity.ok(counts);
    }

    /**
     * Get applications alert (current period vs previous period)
     * GET /api/analytics/applications-alert
     */
    @GetMapping("/applications-alert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getApplicationsAlert(
            @RequestParam(required = false, defaultValue = "7") int days) {
        log.info("REST request to get applications alert for {} days", days);
        
        Map<String, Object> data = analyticsService.getApplicationsAlert(days);
        return ResponseEntity.ok(data);
    }

    /**
     * Get interviews alert (current period vs previous period)
     * GET /api/analytics/interviews-alert
     */
    @GetMapping("/interviews-alert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getInterviewsAlert(
            @RequestParam(required = false, defaultValue = "7") int days) {
        log.info("REST request to get interviews alert for {} days", days);
        
        Map<String, Object> data = analyticsService.getInterviewsAlert(days);
        return ResponseEntity.ok(data);
    }

    /**
     * Get hiring rate alert (current period vs previous period)
     * GET /api/analytics/hiring-alert
     */
    @GetMapping("/hiring-alert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getHiringAlert(
            @RequestParam(required = false, defaultValue = "7") int days) {
        log.info("REST request to get hiring alert for {} days", days);
        
        Map<String, Object> data = analyticsService.getHiringAlert(days);
        return ResponseEntity.ok(data);
    }
}
