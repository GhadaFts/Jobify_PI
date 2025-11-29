package com.application.applicationservice.service;

import com.application.applicationservice.dto.BookmarkDTO;
import com.application.applicationservice.dto.BookmarkRequestDTO;
import com.application.applicationservice.exception.ResourceNotFoundException;
import com.application.applicationservice.model.Bookmark;
import com.application.applicationservice.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    /**
     * Create bookmark
     */
    @Transactional
    public BookmarkDTO createBookmark(String jobSeekerId, BookmarkRequestDTO dto) {
        // Check if bookmark already exists
        if (bookmarkRepository.existsByJobSeekerIdAndJobOfferId(jobSeekerId, dto.getJobOfferId())) {
            throw new IllegalStateException("Bookmark already exists for this job offer");
        }

        Bookmark bookmark = Bookmark.builder()
                .jobSeekerId(jobSeekerId)
                .jobOfferId(dto.getJobOfferId())
                .createdAt(LocalDateTime.now())
                .build();

        Bookmark saved = bookmarkRepository.save(bookmark);
        return mapToDTO(saved);
    }

    /**
     * Get all bookmarks for a job seeker
     */
    public List<BookmarkDTO> getBookmarksByJobSeeker(String jobSeekerId) {
        return bookmarkRepository.findByJobSeekerId(jobSeekerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete bookmark by job offer ID
     */
    @Transactional
    public void deleteBookmark(String jobSeekerId, Long jobOfferId) {
        if (!bookmarkRepository.existsByJobSeekerIdAndJobOfferId(jobSeekerId, jobOfferId)) {
            throw new ResourceNotFoundException("Bookmark not found for this job offer");
        }
        bookmarkRepository.deleteByJobSeekerIdAndJobOfferId(jobSeekerId, jobOfferId);
    }

    /**
     * Check if bookmark exists
     */
    public boolean isBookmarked(String jobSeekerId, Long jobOfferId) {
        return bookmarkRepository.existsByJobSeekerIdAndJobOfferId(jobSeekerId, jobOfferId);
    }

    private BookmarkDTO mapToDTO(Bookmark bookmark) {
        return BookmarkDTO.builder()
                .id(bookmark.getId())
                .jobSeekerId(bookmark.getJobSeekerId())
                .jobOfferId(bookmark.getJobOfferId())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}
