package com.application.applicationservice.repository;

import com.application.applicationservice.model.Application;
import com.application.applicationservice.model.ApplicationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    
    List<Application> findByJobOfferId(Double jobOfferId);
    List<Application> findByJobSeekerId(Double jobSeekerId);
    List<Application> findByStatus(ApplicationStatus status);
    List<Application> findByJobOfferIdAndStatus(Double jobOfferId, ApplicationStatus status);
    List<Application> findByIsFavoriteTrue();
    Boolean existsByJobOfferIdAndJobSeekerId(Double jobOfferId, Double jobSeekerId);
}