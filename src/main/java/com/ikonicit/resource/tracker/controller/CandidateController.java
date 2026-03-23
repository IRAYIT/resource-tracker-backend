package com.ikonicit.resource.tracker.controller;

import com.ikonicit.resource.tracker.dto.CandidateDTO;
import com.ikonicit.resource.tracker.entity.Candidate_Openings;
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

            @RequestParam("cv") MultipartFile cv,

            @RequestParam(value = "coverLetter", required = false) MultipartFile coverLetter,

            @RequestParam(value = "additionalDocuments", required = false) MultipartFile additionalDocuments

    ) throws IOException {

        candidateService.applyForJob(publicUrlKey, payload, cv, coverLetter, additionalDocuments);

        return ResponseEntity.ok("Application submitted successfully");
    }
    @GetMapping("/get/{candidateId}")
    public ResponseEntity<CandidateDTO> getCandidate(@PathVariable Long candidateId) {
        CandidateDTO candidate = candidateService.getCandidate(candidateId);
        return ResponseEntity.ok(candidate);
    }

    @PutMapping(value = "/update/{candidateId}", consumes = "multipart/form-data")
    public ResponseEntity<CandidateDTO> updateCandidate(

            @PathVariable Long candidateId,

            @RequestParam("payload") String payload,

            @RequestParam(value = "resume", required = false) MultipartFile resume,

            @RequestParam(value = "coverLetter", required = false) MultipartFile coverLetter,

            @RequestParam(value = "additionalDocuments", required = false) MultipartFile additionalDocuments
    ) {

        CandidateDTO updatedCandidate = candidateService.updateCandidate(
                candidateId,
                payload,
                resume,
                coverLetter,
                additionalDocuments
        );

        return ResponseEntity.ok(updatedCandidate);
    }

    @GetMapping("/resume/{candidateId}")
    public ResponseEntity<byte[]> getResume(@PathVariable Long candidateId) {
        return candidateService.getCv(candidateId);
    }

    @GetMapping("/cover-letter/{candidateId}")
    public ResponseEntity<byte[]> getCoverLetter(@PathVariable Long candidateId) {
        return candidateService.getCoverLetter(candidateId);
    }

    @GetMapping("/additional-documents/{candidateId}")
    public ResponseEntity<byte[]> getAdditionalDocuments(@PathVariable Long candidateId) {
        return candidateService.getAdditionalDocuments(candidateId);
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
    }

