package com.ikonicit.resource.tracker.service;

import com.google.gson.Gson;
import com.ikonicit.resource.tracker.dto.CandidateDTO;
import com.ikonicit.resource.tracker.entity.Candidate;
import com.ikonicit.resource.tracker.entity.Openings;
import com.ikonicit.resource.tracker.repository.CandidateRepository;
import com.ikonicit.resource.tracker.repository.OpeningsRepository;
import com.ikonicit.resource.tracker.utils.SkillDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class CandidateServiceImpl implements CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private OpeningsRepository openingsRepository;

    @Autowired
    private ResumeParserService parserService;

    public void applyForJob(String publicUrlKey,
                            String payload,
                            MultipartFile resume) {

        Gson gson = new Gson();
        CandidateDTO request = gson.fromJson(payload, CandidateDTO.class);

        Openings opening = openingsRepository
                .findByPublicUrlKey(publicUrlKey)
                .orElseThrow(() -> new RuntimeException("Opening not found"));

        try {

            Candidate candidate = new Candidate();

            // ===============================
            // Candidate Basic Details
            // ===============================

            candidate.setFirstName(request.getFirstName());
            candidate.setLastName(request.getLastName());
            candidate.setEmail(request.getEmail());
            candidate.setPhone(request.getPhone());
            candidate.setExperience(request.getExperience());
            candidate.setExpectedSalary(request.getExpectedSalary());
//            candidate.setLocation(request.getLocation());
//            candidate.setNoticePeriod(request.getNoticePeriod());
//            candidate.setLanguagesKnown(request.getLanguagesKnown());
//            candidate.setVisaStatus(request.getVisaStatus());

            // ===============================
            // Resume Details
            // ===============================

            candidate.setResume(resume.getBytes());
            candidate.setResumeName(resume.getOriginalFilename());
            candidate.setResumeType(resume.getContentType());

            candidate.setOpening(opening);
            candidate.setCreatedAt(LocalDateTime.now());
            candidate.setApplicationStatus("APPLIED");

            // ===============================
            // Resume Parsing
            // ===============================

            String resumeText = normalize(
                    Optional.ofNullable(parserService.extractText(resume)).orElse("")
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
            for(String jdSkill : jdSkills){

                for(String resumeSkill : resumeSkillSet){

                    if(resumeSkill.contains(jdSkill) || jdSkill.contains(resumeSkill)){

                        matchedSkills.add(jdSkill);
                        matchCount++;
                        break;
                    }
                }
            }
            // ===============================
            // Keyword Score
            // ===============================
            double expScore = 100; // default if no experience requirement

            if(opening.getExperience() != null && request.getExperience() != null){

                double requiredExp = opening.getExperience().doubleValue();
                double candidateExp = request.getExperience();

                if(requiredExp > 0){
                    expScore = Math.min(candidateExp / requiredExp, 1) * 100;
                } else {
                    expScore = 100;
                }
            }

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
            // Final Score
            // ===============================
            double bonusScore = 0;

            if(resumeSkillSet.size() > jdSkills.size()){
                bonusScore = 5;
            }
            double skillScore = jdSkills.isEmpty() ? 0 :
                    ((double) matchCount / jdSkills.size()) * 100;

            double finalScore =
                    (skillScore * 0.6) +
                            (expScore * 0.2) +
                            (tfidfScore * 0.2) +
                            bonusScore;
            if(matchCount == 0){
                finalScore = 0;
            }
            if(Double.isNaN(finalScore) || Double.isInfinite(finalScore)){
                finalScore = 0;
            }

            finalScore = Math.max(0, Math.min(finalScore, 100));

        // Round to 2 decimal places
            finalScore = Math.round(finalScore * 100.0) / 100.0;

            candidate.setMatchPercentage(finalScore);
            candidate.setMatchedSkills(
                    "Matched:" + String.join(",", matchedSkills) +
                            " | ResumeSkills:" + String.join(",", resumeSkillSet)
            );            // ===============================
            // Save Candidate
            // ===============================


            candidateRepository.save(candidate);

        } catch (IOException e) {

            throw new RuntimeException("Resume upload failed");
        }
    }
    @Override
    public CandidateDTO getCandidate(Long candidateId) {

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        CandidateDTO dto = new CandidateDTO();

        dto.setFirstName(candidate.getFirstName());
        dto.setLastName(candidate.getLastName());
        dto.setEmail(candidate.getEmail());
        dto.setPhone(candidate.getPhone());
        dto.setExperience(candidate.getExperience());
        dto.setExpectedSalary(candidate.getExpectedSalary());

        dto.setResume(candidate.getResume());
        dto.setResumeName(candidate.getResumeName());
        dto.setResumeType(candidate.getResumeType());
        dto.setApplicationStatus(candidate.getApplicationStatus());

        return dto;
    }

    @Override
    public CandidateDTO updateCandidate(Long candidateId, CandidateDTO candidateDTO) {

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        candidate.setFirstName(candidateDTO.getFirstName());
        candidate.setLastName(candidateDTO.getLastName());
        candidate.setEmail(candidateDTO.getEmail());
        candidate.setPhone(candidateDTO.getPhone());
        candidate.setExperience(candidateDTO.getExperience());
        candidate.setExpectedSalary(candidateDTO.getExpectedSalary());

        // ✅ Update resume
        if (candidateDTO.getResume() != null) {
            candidate.setResume(candidateDTO.getResume());
            candidate.setResumeName(candidateDTO.getResumeName());
            candidate.setResumeType(candidateDTO.getResumeType());
        }

        candidateRepository.save(candidate);

        CandidateDTO response = new CandidateDTO();
        response.setFirstName(candidate.getFirstName());
        response.setLastName(candidate.getLastName());
        response.setEmail(candidate.getEmail());
        response.setPhone(candidate.getPhone());
        response.setExperience(candidate.getExperience());
        response.setExpectedSalary(candidate.getExpectedSalary());
        response.setResume(candidate.getResume());
        response.setResumeName(candidate.getResumeName());
        response.setResumeType(candidate.getResumeType());
        response.setApplicationStatus(candidate.getApplicationStatus());

        return response;
    }

    @Override
    public void updateCandidateStatus(Long candidateId, String applicationStatus) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        candidate.setApplicationStatus(applicationStatus);

        candidateRepository.save(candidate);
    }

    @Override
    public String getCandidateStatus(Long candidateId) {

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        return candidate.getApplicationStatus();
    }

    @Override
    public Candidate getCandidateResume(Long candidateId) {

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (candidate.getResume() == null) {
            throw new RuntimeException("Resume not found");
        }

        return candidate;
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
