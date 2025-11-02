package com.recruiter.recruiterservice.controller;

import com.recruiter.recruiterservice.dto.RecruiterRequestDTO;
import com.recruiter.recruiterservice.dto.RecruiterResponseDTO;
import com.recruiter.recruiterservice.service.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiters")
@RequiredArgsConstructor
public class RecruiterController {

    private final RecruiterService service;

    @GetMapping
    public ResponseEntity<List<RecruiterResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecruiterResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<RecruiterResponseDTO> create(@RequestBody RecruiterRequestDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RecruiterResponseDTO> update(@PathVariable Integer id, @RequestBody RecruiterRequestDTO dto) {
        return ResponseEntity.ok(service.updatePartial(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
