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

    private byte[] resume;

    private String resumeName;

    private String resumeType;

    private String applicationStatus;



}