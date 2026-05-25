package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNamesResponseDTO {

    private Integer id;
    private String resourceName;

    // Nullable — employees with no manager have managerId = null
    // Frontend groupResources() handles null correctly
    private Integer managerId;

    // Set by service layer — how many requested skills this employee matched
    // Higher = better fit — used for sorting on frontend
    private Integer matchCount;

    // Constructor used by JPQL @Query
    // managerId is nullable via CASE WHEN — no implicit inner join
    public ResourceNamesResponseDTO(Integer id, String resourceName, Integer managerId) {
        this.id = id;
        this.resourceName = resourceName;
        this.managerId = managerId;
        this.matchCount = 0;
    }
}