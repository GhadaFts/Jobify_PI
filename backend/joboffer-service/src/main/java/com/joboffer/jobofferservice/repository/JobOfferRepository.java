package com.joboffer.jobofferservice.repository;

import com.joboffer.jobofferservice.model.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long>, JpaSpecificationExecutor<JobOffer> {

    // Custom query methods based on diagram
    List<JobOffer> findByStatus(String status);
    
    List<JobOffer> findByPublished(boolean published);
    
    List<JobOffer> findByType(String type);
    
    List<JobOffer> findByExperience(String experience);
    
    List<JobOffer> findByCompany(String company);
    
    List<JobOffer> findByLocation(String location);
    
    List<JobOffer> findByTitleContainingIgnoreCase(String title);
    
    List<JobOffer> findByJobPositionContainingIgnoreCase(String jobPosition);
    
    List<JobOffer> findByRecruiterId(String recruiterId);
    
    // Analytics queries
    long countByCreatedAtAfter(LocalDateTime date);
    
    List<JobOffer> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}