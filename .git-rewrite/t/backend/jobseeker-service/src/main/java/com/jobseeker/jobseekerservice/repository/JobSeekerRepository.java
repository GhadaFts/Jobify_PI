package com.jobseeker.jobseekerservice.repository;

import com.jobseeker.jobseekerservice.model.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, Integer> {
    Optional<JobSeeker> findByEmail(String email);
    List<JobSeeker> findByDeletedFalse();
    //change isDeletedToTrueById


}