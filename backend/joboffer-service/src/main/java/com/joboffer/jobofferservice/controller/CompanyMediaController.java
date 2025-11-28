package com.joboffer.jobofferservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
public class CompanyMediaController {

    private static final String UPLOAD_DIR = "uploads/company-logos";
    private static final String GATEWAY_URL = "http://localhost:8888"; // adjust if needed

    @PostMapping("/company-logo")
    public ResponseEntity<?> uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "empty file"));
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(Map.of("error", "Only image files allowed"));
            }

            String original = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) ext = original.substring(dot);

            String filename = UUID.randomUUID().toString() + ext;

            Path uploadsPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Files.createDirectories(uploadsPath);

            Path target = uploadsPath.resolve(filename);
            Files.write(target, file.getBytes());

            // Return URL exposed through gateway (gateway routes /joboffer-service/** to this service)
            String url = GATEWAY_URL + "/joboffer-service/" + UPLOAD_DIR + "/" + filename;

            Map<String, String> resp = new HashMap<>();
            resp.put("url", url);
            resp.put("filename", filename);
            return ResponseEntity.ok(resp);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "upload failed"));
        }
    }
}
