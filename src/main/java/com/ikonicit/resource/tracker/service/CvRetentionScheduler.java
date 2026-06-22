package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.entity.CandidateAttachments;
import com.ikonicit.resource.tracker.entity.Candidate_Openings;
import com.ikonicit.resource.tracker.entity.Openings;
import com.ikonicit.resource.tracker.repository.CandidateAttachmentsRepository;
import com.ikonicit.resource.tracker.repository.CandidateRepository;
import com.ikonicit.resource.tracker.repository.OpeningsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Scheduled job that enforces CV retention consent.
 *
 * <p>Runs nightly at 02:00. For every <em>closed</em> job opening it finds
 * all linked candidates who did <strong>not</strong> consent to keep their CV
 * ({@code retainCvForFuture = false} or {@code null}) and wipes the raw CV
 * bytes from {@link CandidateAttachments} while keeping the candidate record
 * intact for reporting purposes.</p>
 *
 * <h3>What "closed" means</h3>
 * The scheduler treats an opening as closed when its {@code status} field
 * equals {@code "CLOSED"} (case-insensitive).  Adjust the
 * {@link OpeningsRepository#findByStatusIgnoreCase} call below if your
 * {@link Openings} entity uses a different field name or enum.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CvRetentionScheduler {

    private final OpeningsRepository            openingsRepository;
    private final CandidateRepository           candidateRepository;
    private final CandidateAttachmentsRepository candidateAttachmentsRepository;

    /**
     * Cron: every night at 02:00 server time.
     * Change the expression if a different cadence is preferred.
     */
    @Scheduled(cron = "0 0 2 1,21 * *")
    @Transactional
    public void purgeNonConsentedCvs() {

        log.info("[CvRetentionScheduler] Starting nightly CV purge job.");

        List<Openings> closedOpenings = openingsRepository.findByStatusIgnoreCase("TERMINATED");
        log.info("[CvRetentionScheduler] Found {} terminated openings", closedOpenings.size()); // ← ADD

        if (closedOpenings.isEmpty()) {
            log.info("[CvRetentionScheduler] No closed openings found. Nothing to purge.");
            return;
        }

        int purgedCount = 0;

        for (Openings opening : closedOpenings) {

            List<Candidate_Openings> candidates = candidateRepository.findByOpening_Id(opening.getId());
            log.info("[CvRetentionScheduler] Opening id={} has {} candidates", opening.getId(), candidates.size()); // ← ADD

            for (Candidate_Openings candidate : candidates) {

                Boolean retain = candidate.getRetainCvForFuture();
                log.info("[CvRetentionScheduler] Candidate id={} retainCvForFuture={}", candidate.getId(), retain); // ← ADD

                if (Boolean.TRUE.equals(retain)) {
                    continue;
                }

                Optional<CandidateAttachments> attachmentsOpt =
                        candidateAttachmentsRepository.findByCandidateOpenings_Id(candidate.getId());
                log.info("[CvRetentionScheduler] Attachments found={}", attachmentsOpt.isPresent()); // ← ADD

                if (attachmentsOpt.isEmpty()) {
                    continue;
                }

                CandidateAttachments attachments = attachmentsOpt.get();
                log.info("[CvRetentionScheduler] CV is null={}", attachments.getCv() == null); // ← ADD

                if (attachments.getCv() == null || attachments.getCv().length == 0) {
                    continue;
                }

                attachments.setCv(null);
                attachments.setCvName("[deleted]");
                attachments.setCvType(null);

                candidateAttachmentsRepository.save(attachments);
                purgedCount++;

                log.info("[CvRetentionScheduler] Purged CV for candidate id={}", candidate.getId());
            }
        }

        log.info("[CvRetentionScheduler] Purge complete. {} CV(s) deleted.", purgedCount);
    }
}