package com.ikonicit.resource.tracker.service;

import com.google.gson.Gson;
import com.ikonicit.resource.tracker.dto.CandidateDTO;
import com.ikonicit.resource.tracker.entity.CandidateAttachments;
import com.ikonicit.resource.tracker.entity.Candidate_Openings;
import com.ikonicit.resource.tracker.entity.ApplicationTracking;
import com.ikonicit.resource.tracker.entity.Openings;
import com.ikonicit.resource.tracker.entity.Resource;
import com.ikonicit.resource.tracker.exception.BadRequestException;
import com.ikonicit.resource.tracker.repository.*;
import com.ikonicit.resource.tracker.utils.SkillDictionary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;

    private final OpeningsRepository openingsRepository;

    private final ResumeParserService parserService;

    private final CandidateAttachmentsRepository candidateAttachmentsRepository;

    private final ResourceRepository resourceRepository;

    private final EmailService emailService;

    private final ApplicationTrackingRepository applicationTrackingRepository;

    public void applyForJob(String publicUrlKey,
                            String payload,
                            MultipartFile cv,
                            MultipartFile coverLetter,
                            MultipartFile additionalDocuments) {

        Gson gson = new Gson();
        CandidateDTO request = gson.fromJson(payload, CandidateDTO.class);

        Openings opening = openingsRepository
                .findByPublicUrlKey(publicUrlKey)
                .orElseThrow(() -> new RuntimeException("Opening not found"));

        try {

            Candidate_Openings candidate = new Candidate_Openings();

            // ===============================
            // Candidate Basic Details
            // ===============================

            candidate.setFirstName(request.getFirstName());
            candidate.setLastName(request.getLastName());
            candidate.setEmail(request.getEmail());
            candidate.setPhone(request.getPhone());
            candidate.setExperience(request.getExperience());
            candidate.setExpectedSalary(request.getExpectedSalary());

            candidate.setLocation(request.getLocation());
            candidate.setNoticePeriod(request.getNoticePeriod());
            candidate.setLanguagesKnown(request.getLanguagesKnown());
            candidate.setVisaStatus(request.getVisaStatus());

            candidate.setOpening(opening);
            candidate.setCreatedAt(LocalDateTime.now());
            candidate.setApplicationStatus("APPLIED");

            // ===============================
            // Resume Parsing
            // ===============================

            String resumeText = normalize(
                    Optional.ofNullable(parserService.extractText(cv)).orElse("")
            );

            Set<String> resumeSkillSet = extractSkills(resumeText);

            // ===============================
            // JD Skills
            // ===============================

            String jdSkillString = opening.getSkill() == null ? "" : opening.getSkill();

            List<String> jdSkills = Arrays.stream(jdSkillString.split(","))
                    .map(s -> normalize(s.trim()))
                    .toList();

            // ===============================
            // Skill Matching
            // ===============================

            Set<String> matchedSkills = new HashSet<>();
            int matchCount = 0;

            for (String jdSkill : jdSkills) {

                for (String resumeSkill : resumeSkillSet) {

                    if (resumeSkill.contains(jdSkill) || jdSkill.contains(resumeSkill)) {

                        matchedSkills.add(jdSkill);
                        matchCount++;
                        break;
                    }
                }
            }

            // ===============================
            // Experience Score
            // ===============================

            double expScore = 100;

            if (opening.getExperience() != null && request.getExperience() != null) {

                double requiredExp = opening.getExperience().doubleValue();
                double candidateExp = request.getExperience();

                if (requiredExp > 0) {
                    expScore = Math.min(candidateExp / requiredExp, 1) * 100;
                }
            }

            // ===============================
            // Job Description
            // ===============================

            String jobDescription = normalize(
                    opening.getName() + " " +
                            opening.getTechnology() + " " +
                            opening.getSkill()
            );

            // ===============================
            // TF-IDF Similarity
            // ===============================

            double tfidfScore = cosineSimilarity(
                    wordFrequency(jobDescription),
                    wordFrequency(resumeText)
            ) * 100;

            // ===============================
            // Final Score Calculation
            // ===============================

            double bonusScore = 0;

            if (resumeSkillSet.size() > jdSkills.size()) {
                bonusScore = 5;
            }

            double skillScore = jdSkills.isEmpty() ? 0 :
                    ((double) matchCount / jdSkills.size()) * 100;

            double finalScore =
                    (skillScore * 0.6) +
                            (expScore * 0.2) +
                            (tfidfScore * 0.2) +
                            bonusScore;

            if (matchCount == 0) {
                finalScore = 0;
            }

            if (Double.isNaN(finalScore) || Double.isInfinite(finalScore)) {
                finalScore = 0;
            }

            finalScore = Math.max(0, Math.min(finalScore, 100));

            finalScore = Math.round(finalScore * 100.0) / 100.0;

            candidate.setMatchPercentage(finalScore);

            candidate.setMatchedSkills(
                    "Matched:" + String.join(",", matchedSkills) +
                            " | ResumeSkills:" + String.join(",", resumeSkillSet)
            );

            // ===============================
            // Save Candidate
            // ===============================

            candidateRepository.save(candidate);

            // ===============================
            // Generate Tracking Token
            // ===============================

            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusDays(60);

            ApplicationTracking tracking = new ApplicationTracking();
            tracking.setApplicationId(candidate.getId());
            tracking.setToken(token);
            tracking.setExpiryTime(expiry);

            applicationTrackingRepository.save(tracking);

           // ===============================
           // Build Tracking Link
            // ===============================

            String trackingLink = "http://localhost:4200/track?token=" + token;
            // 👉 change to your frontend URL

            // ===============================
            // Save Attachments
            // ===============================

            CandidateAttachments attachments = new CandidateAttachments();

            attachments.setCandidateOpenings(candidate);

            /* Resume */
            attachments.setCv(cv.getBytes());
            attachments.setCvName(cv.getOriginalFilename());
            attachments.setCvType(cv.getContentType());

            /* Cover Letter */
            if (coverLetter != null && !coverLetter.isEmpty()) {
                attachments.setCoverLetter(coverLetter.getBytes());
                attachments.setCoverLetterName(coverLetter.getOriginalFilename());
                attachments.setCoverLetterType(coverLetter.getContentType());
            }

            /* Additional Documents */
            if (additionalDocuments != null && !additionalDocuments.isEmpty()) {
                attachments.setAdditionalDocuments(additionalDocuments.getBytes());
                attachments.setAdditionalDocumentName(additionalDocuments.getOriginalFilename());
                attachments.setAdditionalDocumentType(additionalDocuments.getContentType());
            }

            candidateAttachmentsRepository.save(attachments);

            // Send confirmation email
            emailService.sendApplicationConfirmation(
                    candidate.getEmail(),
                    candidate.getFirstName(),
                    opening.getName(),
                    trackingLink
            );
            Resource hr = opening.getCreatedBy();

            if (hr == null || hr.getEmail() == null) {
                throw new RuntimeException("HR email not found for this opening");
            }

            String hrEmail = hr.getEmail();

            emailService.sendHrNotificationWithAttachment(
                    hrEmail,
                    candidate.getFirstName() + " " + candidate.getLastName(),
                    candidate.getEmail(),
                    candidate.getPhone(),
                    opening.getName(),
                    candidate.getMatchPercentage(),
                    cv   // 🔥 important
            );
        } catch (IOException e) {
            throw new RuntimeException("Resume upload failed");
        }
    }
    @Override
    public CandidateDTO getCandidate(Long candidateId) {

        Candidate_Openings candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        CandidateDTO dto = new CandidateDTO();

        // ===============================
        // Candidate Details
        // ===============================

        dto.setFirstName(candidate.getFirstName());
        dto.setLastName(candidate.getLastName());
        dto.setEmail(candidate.getEmail());
        dto.setPhone(candidate.getPhone());
        dto.setExperience(candidate.getExperience());
        dto.setExpectedSalary(candidate.getExpectedSalary());
        dto.setLanguagesKnown(candidate.getLanguagesKnown());
        dto.setNoticePeriod(candidate.getNoticePeriod());
        dto.setVisaStatus(candidate.getVisaStatus());
        dto.setApplicationStatus(candidate.getApplicationStatus());

        return dto;
    }

    @Override
    public CandidateDTO updateCandidate(Long candidateId,
                                        String payload,
                                        MultipartFile resume,
                                        MultipartFile coverLetter,
                                        MultipartFile additionalDocuments) {

        Gson gson = new Gson();
        CandidateDTO candidateDTO = gson.fromJson(payload, CandidateDTO.class);

        Candidate_Openings candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        // ===============================
        // Update Candidate Details
        // ===============================

        candidate.setFirstName(candidateDTO.getFirstName());
        candidate.setLastName(candidateDTO.getLastName());
        candidate.setEmail(candidateDTO.getEmail());
        candidate.setPhone(candidateDTO.getPhone());
        candidate.setExperience(candidateDTO.getExperience());
        candidate.setExpectedSalary(candidateDTO.getExpectedSalary());
        candidate.setLocation(candidateDTO.getLocation());
        candidate.setLanguagesKnown(candidateDTO.getLanguagesKnown());
        candidate.setNoticePeriod(candidateDTO.getNoticePeriod());
        candidate.setVisaStatus(candidateDTO.getVisaStatus());

        candidateRepository.save(candidate);

        // ===============================
        // Get Attachments
        // ===============================

        CandidateAttachments attachments =
                candidateAttachmentsRepository
                        .findByCandidateOpenings(candidate)
                        .orElse(new CandidateAttachments());

        attachments.setCandidateOpenings(candidate);

        try {

            // ===============================
            // Update Resume
            // ===============================

            if (resume != null && !resume.isEmpty()) {
                attachments.setCv(resume.getBytes());
                attachments.setCvName(resume.getOriginalFilename());
                attachments.setCvType(resume.getContentType());
            }

            // ===============================
            // Update Cover Letter
            // ===============================

            if (coverLetter != null && !coverLetter.isEmpty()) {
                attachments.setCoverLetter(coverLetter.getBytes());
                attachments.setCoverLetterType(coverLetter.getContentType());
                attachments.setCoverLetterName(coverLetter.getOriginalFilename());
            }

            // ===============================
            // Update Additional Documents
            // ===============================

            if (additionalDocuments != null && !additionalDocuments.isEmpty()) {
                attachments.setAdditionalDocuments(additionalDocuments.getBytes());
                attachments.setAdditionalDocumentName(additionalDocuments.getOriginalFilename());
                attachments.setAdditionalDocumentType(additionalDocuments.getContentType());
            }

        } catch (IOException e) {
            throw new RuntimeException("File upload failed");
        }

        candidateAttachmentsRepository.save(attachments);

        // ===============================
        // Prepare Response
        // ===============================

        CandidateDTO response = new CandidateDTO();

        response.setFirstName(candidate.getFirstName());
        response.setLastName(candidate.getLastName());
        response.setEmail(candidate.getEmail());
        response.setPhone(candidate.getPhone());
        response.setExperience(candidate.getExperience());
        response.setExpectedSalary(candidate.getExpectedSalary());
        response.setLocation(candidate.getLocation());
        response.setLanguagesKnown(candidate.getLanguagesKnown());
        response.setNoticePeriod(candidate.getNoticePeriod());
        response.setVisaStatus(candidate.getVisaStatus());
        response.setApplicationStatus(candidate.getApplicationStatus());

        return response;
    }

    @Override
    public void updateCandidateStatus(Long candidateId, String applicationStatus) {

        Candidate_Openings candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        // ✅ Validate status (important)
        List<String> validStatuses = List.of("APPLIED", "SHORTLISTED", "REJECTED", "HIRED");

        if (!validStatuses.contains(applicationStatus)) {
            throw new BadRequestException("Invalid status");
        }

        candidate.setApplicationStatus(applicationStatus);

        candidateRepository.save(candidate);

        // ✅ NEW LOGIC: Send regret email
        if ("REJECTED".equalsIgnoreCase(applicationStatus)) {

            try {
                emailService.sendRejectionEmail(
                        candidate.getEmail(),
                        candidate.getFirstName(),
                        candidate.getOpening().getName()
                );
            } catch (Exception e) {
                log.error("Failed to send rejection email", e);
            }
        }
    }

    @Override
    public ResponseEntity<byte[]> getCv(Long candidateId) {
        CandidateAttachments attachments = candidateAttachmentsRepository
                .findByCandidateOpeningsId(candidateId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=" + attachments.getCvName())
                .contentType(MediaType.parseMediaType(attachments.getCvType()))
                .body(attachments.getCv());
    }

    @Override
    public ResponseEntity<byte[]> getCoverLetter(Long candidateId) {

        CandidateAttachments attachments = candidateAttachmentsRepository
                .findByCandidateOpeningsId(candidateId)
                .orElseThrow(() -> new RuntimeException("Cover letter not found"));

        byte[] fileData = attachments.getCoverLetter();

        if (fileData == null) {
            throw new RuntimeException("Cover letter not uploaded");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=" + attachments.getCoverLetterName())
                .contentType(MediaType.parseMediaType(attachments.getCoverLetterType()))
                .body(fileData);
    }
    @Override
    public ResponseEntity<byte[]> getAdditionalDocuments(Long candidateId) {

        CandidateAttachments attachments = candidateAttachmentsRepository
                .findByCandidateOpeningsId(candidateId)
                .orElseThrow(() -> new RuntimeException("Documents not found"));

        byte[] fileData = attachments.getAdditionalDocuments();

        if (fileData == null) {
            throw new RuntimeException("Additional document not uploaded");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=" + attachments.getAdditionalDocumentName())
                .contentType(MediaType.parseMediaType(attachments.getAdditionalDocumentType()))
                .body(fileData);
    }

    @Override
    public Map<String, Object> getCandidateStatusByToken(String token) {

        ApplicationTracking tracking = applicationTrackingRepository
                .findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid link"));

        if (tracking.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link expired");
        }

        Candidate_Openings candidate = candidateRepository
                .findById(tracking.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        return Map.of(
                "name", candidate.getFirstName(),
                "jobTitle", candidate.getOpening().getName(),
                "status", candidate.getApplicationStatus(),
                "matchPercentage", candidate.getMatchPercentage()
        );
    }

    private Map<String,Integer> wordFrequency(String text){

        Map<String,Integer> map = new HashMap<>();

        String[] words = text.toLowerCase().split("\\W+");

        for(String word : words){
            map.put(word,map.getOrDefault(word,0)+1);
        }

        return map;
    }

    private double cosineSimilarity(Map<String,Integer> v1, Map<String,Integer> v2){

        Set<String> words = new HashSet<>();

        words.addAll(v1.keySet());
        words.addAll(v2.keySet());

        double dotProduct = 0;
        double normA = 0;
        double normB = 0;

        for(String word : words){

            int a = v1.getOrDefault(word,0);
            int b = v2.getOrDefault(word,0);

            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

        if(normA == 0 || normB == 0){
            return 0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private String normalize(String text) {

        return text
                .toLowerCase()
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private Set<String> extractSkills(String resumeText) {

        Set<String> detectedSkills = new HashSet<>();

        for(String skill : SkillDictionary.SKILLS){

            String pattern = "\\b" + skill + "\\b";

            if(resumeText.matches(".*" + pattern + ".*")){
                detectedSkills.add(skill);
            }
        }

        return detectedSkills;
    }
}
