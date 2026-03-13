package com.ikonicit.resource.tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_Openings")
@Data
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String location;

    private Integer experience;

    private Double expectedSalary;

    private LocalDateTime createdAt;

    private String resumeName;

    private String resumeType;

    private byte[] resume;

    @Column(name = "application_status")
    private String applicationStatus;

    @ManyToOne
    @JoinColumn(name = "opening_id")
    private Openings opening;


    @Column(name = "match_percentage")
    private Double matchPercentage;

    @Column(name = "matched_skills")
    private String matchedSkills;
}
