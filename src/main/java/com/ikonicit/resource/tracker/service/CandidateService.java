package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.CandidateDTO;
import com.ikonicit.resource.tracker.entity.Candidate;
import org.springframework.web.multipart.MultipartFile;

public interface CandidateService {

    void applyForJob(String publicUrlKey, String payload, MultipartFile resume);

    CandidateDTO getCandidate(Long candidateId);

    CandidateDTO updateCandidate(Long candidateId, CandidateDTO candidateDTO);

    void updateCandidateStatus(Long candidateId, String applicationStatus);

    String getCandidateStatus(Long candidateId);

    Candidate getCandidateResume(Long candidateId);
}
