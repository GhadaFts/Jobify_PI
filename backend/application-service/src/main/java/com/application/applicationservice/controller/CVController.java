package com.application.applicationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Collection;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
@Slf4j
public class CVController {

    @Value("${cv.upload.dir:uploads/cvs}")
    private String uploadDir;

    /**
     * Upload CV file
     * Accessible by JOB_SEEKER only
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Map<String, Object>> uploadCV(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobSeekerId") String jobSeekerId,
            @RequestParam("jobOfferId") Long jobOfferId) {

        log.info("📤 Uploading CV for job seeker: {}, job offer: {}", jobSeekerId, jobOfferId);

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File is empty"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (!isValidFileType(contentType)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid file type. Only PDF, DOC, and DOCX are allowed."));
            }

            // Validate file size (5MB max)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File size exceeds 5MB limit"));
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("📁 Created upload directory: {}", uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            String newFilename = String.format("CV_%s_%s_%s_%s%s",
                    jobSeekerId,
                    jobOfferId,
                    timestamp,
                    uniqueId,
                    fileExtension);

            // Save file
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("✅ CV uploaded successfully: {}", newFilename);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("cvLink", newFilename);
            response.put("fileName", originalFilename);
            response.put("fileSize", file.getSize());
            response.put("uploadedAt", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("❌ Error uploading CV: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    /**
     * Download CV file
     * Manual authorization check - accessible by authenticated users with proper roles
     */
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadCV(@PathVariable String filename) {

        log.info("📥 Downloading CV: {}", filename);

        // Manual authorization check
        if (!isAuthorizedToAccessCV()) {
            log.warn("⚠️ Unauthorized access attempt to CV: {}", filename);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            Path filePath = uploadPath.resolve(filename).normalize();

            // Security check
            if (!filePath.startsWith(uploadPath)) {
                log.warn("⚠️ Security: Attempted path traversal attack with filename: {}", filename);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.warn("⚠️ CV not found or not readable: {}", filename);
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            log.info("✅ CV download started: {}", filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            log.error("❌ Malformed URL for CV: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("❌ Error reading CV file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * View CV file (inline display)
     * Manual authorization check - accessible by authenticated users with proper roles
     */
    @GetMapping("/view/{filename:.+}")
    public ResponseEntity<Resource> viewCV(@PathVariable String filename) {

        log.info("👁️ Viewing CV: {}", filename);

        // Manual authorization check
        if (!isAuthorizedToAccessCV()) {
            log.warn("⚠️ Unauthorized access attempt to CV: {}", filename);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            Path filePath = uploadPath.resolve(filename).normalize();

            // Security check
            if (!filePath.startsWith(uploadPath)) {
                log.warn("⚠️ Security: Attempted path traversal attack with filename: {}", filename);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.warn("⚠️ CV not found or not readable: {}", filename);
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                String extension = getFileExtension(filename).toLowerCase();
                switch (extension) {
                    case ".pdf":
                        contentType = "application/pdf";
                        break;
                    case ".doc":
                        contentType = "application/msword";
                        break;
                    case ".docx":
                        contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                        break;
                    default:
                        contentType = "application/octet-stream";
                }
            }

            log.info("✅ CV retrieved successfully: {} (Content-Type: {})", filename, contentType);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(resource);

        } catch (MalformedURLException e) {
            log.error("❌ Malformed URL for CV: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("❌ Error reading CV file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete CV file
     * Accessible by JOB_SEEKER only (for their own CVs)
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Map<String, String>> deleteCV(@RequestParam String cvLink) {

        log.info("🗑️ Deleting CV: {}", cvLink);

        try {
            Path filePath = Paths.get(uploadDir).resolve(cvLink).normalize();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("✅ CV deleted successfully: {}", cvLink);
                return ResponseEntity.ok(Map.of("message", "CV deleted successfully"));
            } else {
                log.warn("⚠️ CV not found: {}", cvLink);
                return ResponseEntity.notFound().build();
            }

        } catch (IOException e) {
            log.error("❌ Error deleting CV: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to delete CV: " + e.getMessage()));
        }
    }

    /**
     * Check if user is authorized to access CV
     * Allows JOB_SEEKER, RECRUITER, and ADMIN roles
     */
    private boolean isAuthorizedToAccessCV() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            log.warn("⚠️ No authentication found");
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        boolean hasRole = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_JOB_SEEKER")
                        || a.getAuthority().equals("ROLE_RECRUITER")
                        || a.getAuthority().equals("ROLE_ADMIN"));

        if (!hasRole) {
            log.warn("⚠️ User does not have required role. Authorities: {}", authorities);
        }

        return hasRole;
    }

    /**
     * Validate file type
     */
    private boolean isValidFileType(String contentType) {
        return contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }
}