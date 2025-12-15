package com.jobify.interview.service;

import com.jobify.interview.dto.*;
import com.jobify.interview.entity.Interview;
import com.jobify.interview.entity.InterviewStatus;
import com.jobify.interview.entity.InterviewType;
import com.jobify.interview.exception.InterviewNotFoundException;
import com.jobify.interview.exception.InvalidInterviewDataException;
import com.jobify.interview.feign.ApplicationFeignClient;
import com.jobify.interview.feign.UserFeignClient;
import com.jobify.interview.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationFeignClient applicationFeignClient;
    private final UserFeignClient userFeignClient;
    private final InterviewNotificationService notificationService;

    @Transactional
    public InterviewResponseDTO scheduleInterview(InterviewRequestDTO requestDTO) {
        log.info("Scheduling interview for application: {}", requestDTO.getApplicationId());

        // Validate interview data
        validateInterviewData(requestDTO);

        // Get JWT token from SecurityContext
        String token = getAuthorizationToken();

        // Verify application exists
        try {
            applicationFeignClient.getById(requestDTO.getApplicationId(), token);
        } catch (Exception e) {
            log.error("Failed to fetch application: {}", e.getMessage());
            throw new InvalidInterviewDataException("Application not found: " + requestDTO.getApplicationId());
        }

        // Check for existing active interviews
        List<InterviewStatus> activeStatuses = Arrays.asList(
                InterviewStatus.SCHEDULED,
                InterviewStatus.RESCHEDULED
        );

        boolean hasActiveInterview = interviewRepository
                .existsByApplicationIdAndJobSeekerId(requestDTO.getApplicationId(), requestDTO.getJobSeekerId());

        if (hasActiveInterview) {
            throw new InvalidInterviewDataException(
                    "An interview already exists for this application with that candidate"
            );
        }

        // Create interview
        Interview interview = mapToEntity(requestDTO);
        Interview savedInterview = interviewRepository.save(interview);

        // Update application status to INTERVIEW_SCHEDULED
        try {
            applicationFeignClient.updateStatus(
                    requestDTO.getApplicationId(),
                    "INTERVIEW_SCHEDULED",
                    token  // ✅ Add token here too
            );
        } catch (Exception e) {
            log.error("Failed to update application status", e);
        }

        // Send notification
        notificationService.sendInterviewScheduledNotification(savedInterview);

        log.info("Interview scheduled successfully with ID: {}", savedInterview.getId());
        return mapToResponseDTO(savedInterview);
    }

    // ✅ Add this helper method at the bottom of your service class
    private String getAuthorizationToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Authentication type: {}", authentication != null ? authentication.getClass().getName() : "null");

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
            String token = "Bearer " + jwtToken.getToken().getTokenValue();
            log.info("Token extracted: {}", token.substring(0, Math.min(20, token.length())) + "...");
            return token;
        }

        log.error("No JWT authentication found in SecurityContext");
        throw new RuntimeException("No authentication token found");
    }
    @Transactional(readOnly = true)
    public InterviewResponseDTO getInterviewById(Long id) {
        log.info("Fetching interview with ID: {}", id);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new InterviewNotFoundException("Interview not found with ID: " + id));
        return mapToResponseDTO(interview);
    }

    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> getInterviewsByApplicationId(String applicationId) {
        log.info("Fetching interviews for application: {}", applicationId);
        List<Interview> interviews = interviewRepository.findByApplicationId(applicationId);
        return interviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> getInterviewsByUserId(String userId) {
        log.info("Fetching interviews for user: {}", userId);
        List<Interview> interviews = interviewRepository.findByJobSeekerId(userId);
        return interviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> getUpcomingInterviews(String userId) {
        log.info("Fetching upcoming interviews for user: {}", userId);
        List<InterviewStatus> activeStatuses = Arrays.asList(
                InterviewStatus.SCHEDULED,
                InterviewStatus.RESCHEDULED
        );

        List<Interview> interviews = interviewRepository
                .findUpcomingInterviewsByUserId(userId, LocalDateTime.now(), activeStatuses);

        return interviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> getInterviewsByRecruiterId(String recruiterId) {
        log.info("Fetching interviews for recruiter: {}", recruiterId);
        List<Interview> interviews = interviewRepository.findByRecruiterId(recruiterId);
        return interviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> getUpcomingInterviewsByRecruiterId(String recruiterId) {
        log.info("Fetching upcoming interviews for recruiter: {}", recruiterId);
        List<InterviewStatus> activeStatuses = Arrays.asList(
                InterviewStatus.SCHEDULED,
                InterviewStatus.RESCHEDULED
        );

        List<Interview> interviews = interviewRepository
                .findUpcomingInterviewsByRecruiterId(recruiterId, LocalDateTime.now(), activeStatuses);

        return interviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public InterviewResponseDTO updateInterview(Long id, InterviewUpdateDTO updateDTO) {
        log.info("Updating interview with ID: {}", id);

        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new InterviewNotFoundException("Interview not found with ID: " + id));

        // Validate if interview can be updated
        if (interview.getStatus().isFinal()) {
            throw new InvalidInterviewDataException(
                    "Cannot update interview with final status: " + interview.getStatus()
            );
        }

        // Update fields
        boolean wasRescheduled = false;

        if (updateDTO.getScheduledDate() != null &&
                !updateDTO.getScheduledDate().equals(interview.getScheduledDate())) {
            interview.setScheduledDate(updateDTO.getScheduledDate());
            interview.setStatus(InterviewStatus.RESCHEDULED);
            wasRescheduled = true;
        }

        if (updateDTO.getDuration() != null) {
            interview.setDuration(updateDTO.getDuration());
        }

        if (updateDTO.getLocation() != null) {
            interview.setLocation(updateDTO.getLocation());
        }

        if (updateDTO.getInterviewType() != null) {
            interview.setInterviewType(updateDTO.getInterviewType());
        }

        if (updateDTO.getNotes() != null) {
            interview.setNotes(updateDTO.getNotes());
        }

        if (updateDTO.getMeetingLink() != null) {
            interview.setMeetingLink(updateDTO.getMeetingLink());
        }

        if (updateDTO.getStatus() != null && !wasRescheduled) {
            interview.setStatus(updateDTO.getStatus());
        }

        Interview updatedInterview = interviewRepository.save(interview);

        // Send notification
        notificationService.sendInterviewUpdatedNotification(updatedInterview);

        log.info("Interview updated successfully: {}", id);
        return mapToResponseDTO(updatedInterview);
    }

    @Transactional
    public void cancelInterview(Long id) {
        log.info("Cancelling interview with ID: {}", id);

        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new InterviewNotFoundException("Interview not found with ID: " + id));

        if (interview.getStatus().isFinal()) {
            throw new InvalidInterviewDataException(
                    "Cannot cancel interview with final status: " + interview.getStatus()
            );
        }

        interview.setStatus(InterviewStatus.CANCELLED);
        interviewRepository.save(interview);

        // Send notification
        notificationService.sendInterviewCancelledNotification(interview);

        log.info("Interview cancelled successfully: {}", id);
    }

    public void validateInterviewData(InterviewRequestDTO data) {
        if (data.getScheduledDate().isBefore(LocalDateTime.now())) {
            throw new InvalidInterviewDataException("Interview cannot be scheduled in the past");
        }

        if (data.getInterviewType() == InterviewType.REMOTE &&
                (data.getMeetingLink() == null || data.getMeetingLink().isBlank())) {
            throw new InvalidInterviewDataException("Meeting link is required for remote interviews");
        }

        if (data.getInterviewType() == InterviewType.ON_SITE &&
                (data.getLocation() == null || data.getLocation().isBlank())) {
            throw new InvalidInterviewDataException("Location is required for on-site interviews");
        }
    }

    @Transactional
    public void sendInterviewReminders() {
        log.info("Sending interview reminders");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        List<Interview> upcomingInterviews = interviewRepository
                .findInterviewsForReminders(now, tomorrow, InterviewStatus.SCHEDULED);

        upcomingInterviews.stream()
                .filter(Interview::shouldSendReminder)
                .forEach(notificationService::sendInterviewScheduledNotification);

        log.info("Sent {} interview reminders", upcomingInterviews.size());
    }

    // Mapping methods
    private Interview mapToEntity(InterviewRequestDTO dto) {
        Interview interview = new Interview();
        interview.setApplicationId(dto.getApplicationId());
        interview.setJobSeekerId(dto.getJobSeekerId());
        interview.setRecruiterId(dto.getRecruiterId());
        interview.setScheduledDate(dto.getScheduledDate());
        interview.setDuration(dto.getDuration());
        interview.setLocation(dto.getLocation());
        interview.setInterviewType(dto.getInterviewType());
        interview.setNotes(dto.getNotes());
        interview.setMeetingLink(dto.getMeetingLink());
        interview.setStatus(InterviewStatus.SCHEDULED);
        return interview;
    }

    private InterviewResponseDTO mapToResponseDTO(Interview interview) {
        InterviewResponseDTO dto = new InterviewResponseDTO();
        dto.setId(interview.getId());
        dto.setApplicationId(interview.getApplicationId());
        dto.setJobSeekerId(interview.getJobSeekerId());
        dto.setRecruiterId(interview.getRecruiterId());
        dto.setScheduledDate(interview.getScheduledDate());
        dto.setDuration(interview.getDuration());
        dto.setLocation(interview.getLocation());
        dto.setInterviewType(interview.getInterviewType());
        dto.setStatus(interview.getStatus());
        dto.setNotes(interview.getNotes());
        dto.setMeetingLink(interview.getMeetingLink());
        dto.setCreatedAt(interview.getCreatedAt());
        dto.setUpdatedAt(interview.getUpdatedAt());

        // ✅ Fetch user details using UserFeignClient
        try {
            UserDetailsDTO jobSeekerDetails = userFeignClient
                    .getUserById(interview.getJobSeekerId())
                    .getBody();
            dto.setJobSeekerDetails(jobSeekerDetails);

            UserDetailsDTO recruiterDetails = userFeignClient
                    .getUserById(interview.getRecruiterId())
                    .getBody();
            dto.setRecruiterDetails(recruiterDetails);
        } catch (Exception e) {
            log.error("Failed to fetch user details: {}", e.getMessage());
            // Continue without user details if fetch fails
        }

        return dto;
    }
}