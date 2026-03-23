package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.entity.Candidate_Openings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate_Openings, Long> {

    boolean existsByEmailAndOpeningId(String email, Integer openingId);
}
