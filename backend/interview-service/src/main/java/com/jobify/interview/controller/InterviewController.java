package com.jobify.interview.controller;

import com.jobify.interview.dto.InterviewRequestDTO;
import com.jobify.interview.dto.InterviewResponseDTO;
import com.jobify.interview.dto.InterviewUpdateDTO;
import com.jobify.interview.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<InterviewResponseDTO> scheduleInterview(
            @Valid @RequestBody InterviewRequestDTO requestDTO) {
        log.info("REST request to schedule interview for application: {}",
                requestDTO.getApplicationId());

        InterviewResponseDTO response = interviewService.scheduleInterview(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewResponseDTO> getInterviewById(@PathVariable Long id) {
        log.info("REST request to get interview: {}", id);

        InterviewResponseDTO response = interviewService.getInterviewById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<InterviewResponseDTO>> getInterviewsByApplicationId(
            @PathVariable Long applicationId) {
        log.info("REST request to get interviews for application: {}", applicationId);

        List<InterviewResponseDTO> response =
                interviewService.getInterviewsByApplicationId(applicationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InterviewResponseDTO>> getInterviewsByUserId(
            @PathVariable String userId) {
        log.info("REST request to get interviews for user: {}", userId);

        List<InterviewResponseDTO> response =
                interviewService.getInterviewsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upcoming/{userId}")
    public ResponseEntity<List<InterviewResponseDTO>> getUpcomingInterviews(
            @PathVariable String userId) {
        log.info("REST request to get upcoming interviews for user: {}", userId);

        List<InterviewResponseDTO> response =
                interviewService.getUpcomingInterviews(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<List<InterviewResponseDTO>> getInterviewsByRecruiterId(
            @PathVariable String recruiterId) {
        log.info("REST request to get interviews for recruiter: {}", recruiterId);

        List<InterviewResponseDTO> response =
                interviewService.getInterviewsByRecruiterId(recruiterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recruiter/{recruiterId}/upcoming")
    public ResponseEntity<List<InterviewResponseDTO>> getUpcomingInterviewsByRecruiterId(
            @PathVariable String recruiterId) {
        log.info("REST request to get upcoming interviews for recruiter: {}", recruiterId);

        List<InterviewResponseDTO> response =
                interviewService.getUpcomingInterviewsByRecruiterId(recruiterId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterviewResponseDTO> updateInterview(
            @PathVariable Long id,
            @Valid @RequestBody InterviewUpdateDTO updateDTO) {
        log.info("REST request to update interview: {}", id);

        InterviewResponseDTO response = interviewService.updateInterview(id, updateDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelInterview(@PathVariable Long id) {
        log.info("REST request to cancel interview: {}", id);

        interviewService.cancelInterview(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reminders/send")
    public ResponseEntity<Void> sendInterviewReminders() {
        log.info("REST request to send interview reminders");

        interviewService.sendInterviewReminders();
        return ResponseEntity.ok().build();
    }
}