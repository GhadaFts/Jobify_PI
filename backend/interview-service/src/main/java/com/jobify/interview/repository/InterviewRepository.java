package com.jobify.interview.repository;

import com.jobify.interview.entity.Interview;
import com.jobify.interview.entity.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByApplicationId(Long applicationId);

    List<Interview> findByJobSeekerId(String jobSeekerId);

    List<Interview> findByRecruiterId(String recruiterId);

    List<Interview> findByScheduledDateBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT i FROM Interview i WHERE i.jobSeekerId = :userId " +
            "AND i.scheduledDate > :now AND i.status IN :activeStatuses " +
            "ORDER BY i.scheduledDate ASC")
    List<Interview> findUpcomingInterviewsByUserId(
            @Param("userId") String userId,
            @Param("now") LocalDateTime now,
            @Param("activeStatuses") List<InterviewStatus> activeStatuses
    );

    @Query("SELECT i FROM Interview i WHERE i.recruiterId = :recruiterId " +
            "AND i.scheduledDate > :now AND i.status IN :activeStatuses " +
            "ORDER BY i.scheduledDate ASC")
    List<Interview> findUpcomingInterviewsByRecruiterId(
            @Param("recruiterId") String recruiterId,
            @Param("now") LocalDateTime now,
            @Param("activeStatuses") List<InterviewStatus> activeStatuses
    );

    @Query("SELECT i FROM Interview i WHERE i.scheduledDate BETWEEN :start AND :end " +
            "AND i.status = :status")
    List<Interview> findInterviewsForReminders(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") InterviewStatus status
    );

    boolean existsByApplicationIdAndStatusIn(
            Long applicationId,
            List<InterviewStatus> statuses
    );

    @Query("SELECT COUNT(i) FROM Interview i WHERE i.recruiterId = :recruiterId " +
            "AND i.status = :status")
    Long countByRecruiterIdAndStatus(
            @Param("recruiterId") String recruiterId,
            @Param("status") InterviewStatus status
    );
}