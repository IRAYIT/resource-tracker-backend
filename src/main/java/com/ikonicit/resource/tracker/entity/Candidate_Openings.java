package com.ikonicit.resource.tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_Openings")
@Data
public class Candidate_Openings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String location;

    private Integer experience;

    @Column(name = "current_salary_currency")
    private String currentSalaryCurrency;
    
    @Column(name = "current_salary")
    private String currentSalary;

    @Column(name = "expected_salary_currency")
    private String expectedSalaryCurrency;

    private String expectedSalary;

    private LocalDateTime createdAt;

    @Column(name = "languages_known")
    private String languagesKnown;

    @Column(name = "notice_period")
    private Integer noticePeriod;

    @Column(name = "visa_status")
    private String visaStatus;

    @Column(name = "application_status")
    private String applicationStatus;

    @ManyToOne
    @JoinColumn(name = "opening_id")
    private Openings opening;

    @Column(name = "match_percentage")
    private Double matchPercentage;

    @Column(name = "matched_skills")
    private String matchedSkills;

    private String employmentType;

    private String source;

    @OneToOne(mappedBy = "candidateOpenings", fetch = FetchType.LAZY)
    private CandidateAttachments attachments;


}
