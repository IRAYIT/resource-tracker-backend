package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNamesResponseDTO {

    private Integer id;
    private String  resourceName;
    private Integer matchCount;    // how many requested skills this employee matched
    private String  matchedSkills; // e.g. "React, TypeScript, Redux" — set by service layer

    // Constructor used by JPQL @Query in ResourceRepository
    // Signature reduced to (id, resourceName) — managerId removed
    public ResourceNamesResponseDTO(Integer id, String resourceName) {
        this.id           = id;
        this.resourceName = resourceName;
        this.matchCount   = 0;
        this.matchedSkills = "";
    }
}