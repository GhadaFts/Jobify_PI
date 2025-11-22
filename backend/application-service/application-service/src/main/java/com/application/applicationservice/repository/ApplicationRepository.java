package com.application.applicationservice.repository;

import com.application.applicationservice.model.Application;
import com.application.applicationservice.model.ApplicationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    
    List<Application> findByJobOfferId(Integer jobOfferId);
    List<Application> findByJobSeekerId(Integer jobSeekerId);
    List<Application> findByStatus(ApplicationStatus status);
    List<Application> findByJobOfferIdAndStatus(Integer jobOfferId, ApplicationStatus status);
    List<Application> findByIsFavoriteTrue();
    Boolean existsByJobOfferIdAndJobSeekerId(Integer jobOfferId, Integer jobSeekerId);
}