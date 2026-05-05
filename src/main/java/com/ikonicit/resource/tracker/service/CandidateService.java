package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.CandidateDTO;
import com.ikonicit.resource.tracker.entity.Candidate_Openings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CandidateService {

    public void applyForJob(String publicUrlKey,
                            String payload,
                            MultipartFile cv,
                            MultipartFile coverLetter,
                            MultipartFile additionalDocuments);
    CandidateDTO getCandidate(Long candidateId);

    CandidateDTO updateCandidate(Long candidateId,
                                 String payload,
                                 MultipartFile resume,
                                 MultipartFile coverLetter,
                                 MultipartFile additionalDocuments);

    void updateCandidateStatus(Long candidateId, String applicationStatus);

    ResponseEntity<byte[]> getCv(Long candidateId);

    ResponseEntity<byte[]> getCoverLetter(Long candidateId);

    ResponseEntity<byte[]> getAdditionalDocuments(Long candidateId);

    Object getCandidateStatusByToken(String token);

    List<CandidateDTO> getAllCandidates();

    void deleteCandidate(Long candidateId);

    List<CandidateDTO> getCandidatesByOpening(Integer openingId);
}
