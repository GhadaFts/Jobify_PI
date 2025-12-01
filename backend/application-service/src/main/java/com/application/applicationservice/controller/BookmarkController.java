package com.application.applicationservice.controller;

import com.application.applicationservice.dto.BookmarkDTO;
import com.application.applicationservice.dto.BookmarkRequestDTO;
import com.application.applicationservice.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * Create bookmark - Only JOB_SEEKER can create bookmarks
     */
    @PostMapping
    // Allow both job seekers and recruiters to call this endpoint for now.
    // If bookmarks should be job-seeker-only, revert to hasRole('JOB_SEEKER')
    @PreAuthorize("hasAnyRole('JOB_SEEKER','RECRUITER')")
    public ResponseEntity<BookmarkDTO> createBookmark(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody BookmarkRequestDTO dto) {

        String jobSeekerId = jwt.getSubject();
        BookmarkDTO bookmark = bookmarkService.createBookmark(jobSeekerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookmark);
    }

    /**
     * Get all bookmarks for current job seeker
     */
    @GetMapping("/my-bookmarks")
    @PreAuthorize("hasAnyRole('JOB_SEEKER','RECRUITER')")
    public ResponseEntity<List<BookmarkDTO>> getMyBookmarks(
            @AuthenticationPrincipal Jwt jwt) {

        String jobSeekerId = jwt.getSubject();
        List<BookmarkDTO> bookmarks = bookmarkService.getBookmarksByJobSeeker(jobSeekerId);
        return ResponseEntity.ok(bookmarks);
    }

    /**
     * Check if job is bookmarked
     */
    @GetMapping("/check/{jobOfferId}")
    @PreAuthorize("hasAnyRole('JOB_SEEKER','RECRUITER')")
    public ResponseEntity<Boolean> isBookmarked(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long jobOfferId) {

        String jobSeekerId = jwt.getSubject();
        boolean isBookmarked = bookmarkService.isBookmarked(jobSeekerId, jobOfferId);
        return ResponseEntity.ok(isBookmarked);
    }

    /**
     * Delete bookmark by job offer ID
     */
    @DeleteMapping("/job/{jobOfferId}")
    @PreAuthorize("hasAnyRole('JOB_SEEKER','RECRUITER')")
    public ResponseEntity<Void> deleteBookmark(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long jobOfferId) {

        String jobSeekerId = jwt.getSubject();
        bookmarkService.deleteBookmark(jobSeekerId, jobOfferId);
        return ResponseEntity.noContent().build();
    }
}