package com.ikonicit.resource.tracker.controller;

import com.ikonicit.resource.tracker.dto.CandidateDTO;
import com.ikonicit.resource.tracker.entity.Candidate;
import com.ikonicit.resource.tracker.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
    @RequestMapping("/api/public/apply")
    public class CandidateController {

        @Autowired
        private CandidateService candidateService;

    @PostMapping(value = "/{publicUrlKey}", consumes = "multipart/form-data")
    public ResponseEntity<String> applyForJob(

            @PathVariable String publicUrlKey,

            @RequestParam("payload") String payload,

            @RequestParam("resume") MultipartFile resume) throws IOException {

        candidateService.applyForJob(publicUrlKey, payload, resume);

        return ResponseEntity.ok("Application submitted successfully");
    }

    @GetMapping("/get/{candidateId}")
    public ResponseEntity<CandidateDTO> getCandidate(@PathVariable Long candidateId) {
        CandidateDTO candidate = candidateService.getCandidate(candidateId);
        return ResponseEntity.ok(candidate);
    }

    @PutMapping("/update/{candidateId}")
    public ResponseEntity<CandidateDTO> updateCandidate(
            @PathVariable Long candidateId,
            @RequestBody CandidateDTO candidateDTO) {

        CandidateDTO updatedCandidate = candidateService.updateCandidate(candidateId, candidateDTO);
        return ResponseEntity.ok(updatedCandidate);
    }

    @PutMapping("/status/{candidateId}")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long candidateId,
            @RequestBody CandidateDTO candidateDTO) {

        candidateService.updateCandidateStatus(candidateId, candidateDTO.getApplicationStatus());

        return ResponseEntity.ok("Status updated successfully");
    }

    @GetMapping("/status/{candidateId}")
    public ResponseEntity<String> getCandidateStatus(@PathVariable Long candidateId) {

        String status = candidateService.getCandidateStatus(candidateId);

        return ResponseEntity.ok(status);
    }
    @GetMapping("/candidate/resume/{candidateId}")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long candidateId) {

        Candidate candidate = candidateService.getCandidateResume(candidateId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + candidate.getResumeName() + "\"")
                .contentType(MediaType.parseMediaType(candidate.getResumeType()))
                .body(candidate.getResume());
    }
    }

