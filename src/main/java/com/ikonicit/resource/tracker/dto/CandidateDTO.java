package com.ikonicit.resource.tracker.dto;

import lombok.Data;

@Data
public class CandidateDTO {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Integer experience;

    private Double expectedSalary;

    private String location;

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



}