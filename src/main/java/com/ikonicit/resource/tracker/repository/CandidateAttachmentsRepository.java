package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.entity.CandidateAttachments;
import com.ikonicit.resource.tracker.entity.Candidate_Openings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateAttachmentsRepository extends JpaRepository<CandidateAttachments, Long> {

    Optional<CandidateAttachments> findByCandidateOpenings(Candidate_Openings candidate);

    Optional<CandidateAttachments> findByCandidateOpeningsId(Long candidateId);
}
