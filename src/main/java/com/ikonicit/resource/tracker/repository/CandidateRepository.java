package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.dto.CandidateDTO;
import com.ikonicit.resource.tracker.entity.Candidate_Openings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate_Openings, Long> {

    boolean existsByEmailAndOpeningId(String email, Integer openingId);

    @Query("SELECT new com.ikonicit.resource.tracker.dto.CandidateDTO(" +
            "c.id, c.firstName, c.lastName, c.email, c.phone, c.experience, " +
            "c.expectedSalary, c.location, c.languagesKnown, c.noticePeriod, " +
            "c.visaStatus, " +
            "null, null, null, " +
            "a.cvName, a.cvType, a.coverLetterName, a.coverLetterType, " +
            "a.additionalDocumentName, a.additionalDocumentType, " +
            "c.applicationStatus, c.source, c.employmentType) " +
            "FROM Candidate_Openings c " +
            "LEFT JOIN c.attachments a " +
            "ORDER BY c.firstName ASC")
    List<CandidateDTO> getAllCandidatesWithAttachments();
    }
