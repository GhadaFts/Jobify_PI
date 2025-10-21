package com.jobseeker.jobseekerservice.controller;


import com.jobseeker.jobseekerservice.dto.JobSeekerRequestDTO;
import com.jobseeker.jobseekerservice.dto.JobSeekerResponseDTO;
import com.jobseeker.jobseekerservice.service.JobSeekerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobseekers")
public class JobSeekerController {

    @Autowired
    private final JobSeekerService service;
    public JobSeekerController(JobSeekerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<JobSeekerResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobSeekerResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<JobSeekerResponseDTO> create(@RequestBody JobSeekerRequestDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
