package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.dto.CandidateDTO;
import com.ikonicit.resource.tracker.entity.Candidate_Openings;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate_Openings, Long> {

    boolean existsByEmailAndOpeningId(String email, Integer openingId);

    @Query("SELECT new com.ikonicit.resource.tracker.dto.CandidateDTO(" +
            "c.id, " +
            "c.firstName, " +
            "c.lastName, " +
            "c.email, " +
            "c.phone, " +
            "c.experience, " +

            "c.currentSalaryCurrency, " +
            "c.currentSalary, " +

            "c.expectedSalaryCurrency, " +
            "c.expectedSalary, " +

            "c.location, " +
            "c.skills, " +
            "c.languagesKnown, " +
            "c.noticePeriod, " +
            "c.visaStatus, " +

            "null, " +   // cv
            "null, " +   // coverLetter
            "null, " +   // additionalDocuments

            "a.cvName, " +
            "a.cvType, " +
            "a.coverLetterName, " +
            "a.coverLetterType, " +
            "a.additionalDocumentName, " +
            "a.additionalDocumentType, " +

            "c.applicationStatus, " +
            "c.source, " +
            "c.employmentType, " +
            "c.retainCvForFuture " +
            // ← added

            ") " +
            "FROM Candidate_Openings c " +
            "LEFT JOIN c.attachments a " +
            "ORDER BY c.firstName ASC")   // ← changed from firstName ASC
    List<CandidateDTO> getAllCandidatesWithAttachments();

    @Query(value = "SELECT * FROM candidate_Openings WHERE id = :id AND is_deleted = true", nativeQuery = true)
    Optional<Candidate_Openings> findDeletedCandidateById(@Param("id") Long id);

    @Query("SELECT COUNT(c) FROM Candidate_Openings c WHERE c.opening.id = :openingId")
    Long countByOpeningId(@Param("openingId") Integer openingId);

    List<Candidate_Openings> findByOpening_Id(Integer openingId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE candidate_Openings SET is_deleted = false, deleted_at = null " +
            "WHERE id = :id AND is_deleted = true", nativeQuery = true)
    int restoreById(@Param("id") Long id);

    // --- Repository ---
    @Query(value = "SELECT * FROM candidate_Openings WHERE is_deleted = true", nativeQuery = true)
    List<Candidate_Openings> findAllDeleted();
    }
