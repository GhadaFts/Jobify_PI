package com.joboffer.jobofferservice.repository;

import com.joboffer.jobofferservice.model.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobOfferRepository extends JpaRepository<JobOffer, Integer> {
    List<JobOffer> findByRecruiterId(Integer recruiterId);
}
