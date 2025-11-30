package com.application.applicationservice.repository;

import com.application.applicationservice.model.Bookmark;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends MongoRepository<Bookmark, String> {

    List<Bookmark> findByJobSeekerId(String jobSeekerId);

    Optional<Bookmark> findByJobSeekerIdAndJobOfferId(String jobSeekerId, Long jobOfferId);

    boolean existsByJobSeekerIdAndJobOfferId(String jobSeekerId, Long jobOfferId);

    void deleteByJobSeekerIdAndJobOfferId(String jobSeekerId, Long jobOfferId);
}