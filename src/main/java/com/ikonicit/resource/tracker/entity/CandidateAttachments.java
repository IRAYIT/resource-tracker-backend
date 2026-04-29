package com.ikonicit.resource.tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "candidate_attachments")
@Getter
@Setter
public class CandidateAttachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long candidateAttachmentId;

    @Column(name = "CV", columnDefinition = "bytea")
    private byte[] cv;

    @Column(columnDefinition = "bytea")
    private byte[] coverLetter;

    @Column(columnDefinition = "bytea")
    private byte[] additionalDocuments;

    private String cvName;

    private String cvType;

    @Column(name = "cover_letter_name")
    private String coverLetterName;

    @Column(name = "cover_letter_type")
    private String coverLetterType;

    @Column(name = "additional_document_name")
    private String additionalDocumentName;

    @Column(name = "additional_document_type")
    private String additionalDocumentType;

    @OneToOne
    @JoinColumn(name = "candidate_id")
    private Candidate_Openings candidateOpenings;
}
