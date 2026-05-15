package com.ikonicit.resource.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Integer experience;

    private String currentSalaryCurrency;

    private String currentSalary;

    private String expectedSalaryCurrency;

    private String expectedSalary;

    private String location;

    private String skills;

    private String languagesKnown;

    private Integer noticePeriod;

    private String visaStatus;

    private byte[] cv;

    private byte[] coverLetter;

    private byte[] additionalDocuments;

    private String cvName;

    private String cvType;

    private String coverLetterName;

    private String coverLetterType;

    private String additionalDocumentName;

    private String additionalDocumentType;

    private String applicationStatus;

    private String source;

    private String employmentType;

    /**
     * Whether the candidate consented to keeping their CV for future openings.
     * Sent from the application form and persisted on the candidate record.
     */
    private Boolean retainCvForFuture;
}