package com.joboffer.jobofferservice.controller;

import com.joboffer.jobofferservice.dto.JobOfferRequestDTO;
import com.joboffer.jobofferservice.dto.JobOfferResponseDTO;
import com.joboffer.jobofferservice.service.JobOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/joboffers")
@RequiredArgsConstructor
public class JobOfferController {

    private final JobOfferService service;

    @PostMapping
    public ResponseEntity<JobOfferResponseDTO> create(@RequestBody JobOfferRequestDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<JobOfferResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOfferResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<JobOfferResponseDTO> updatePartial(@PathVariable Integer id, @RequestBody JobOfferRequestDTO dto) {
        return ResponseEntity.ok(service.updatePartial(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
