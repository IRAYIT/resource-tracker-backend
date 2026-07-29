package com.ikonicit.resource.tracker.dto;

import com.ikonicit.resource.tracker.entity.Candidate_Openings;

import java.time.LocalDateTime;

public record DeletedCandidateDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Integer experience,
        String skills,
        String applicationStatus,
        LocalDateTime deletedAt
) {
    public static DeletedCandidateDto from(Candidate_Openings c) {
        return new DeletedCandidateDto(
                c.getId(),
                c.getFirstName(),
                c.getLastName(),
                c.getEmail(),
                c.getPhone(),
                c.getExperience(),
                c.getSkills(),
                c.getApplicationStatus(),
                c.getDeletedAt()
        );
    }
}