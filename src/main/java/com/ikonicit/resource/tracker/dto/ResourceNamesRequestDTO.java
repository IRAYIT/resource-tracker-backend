package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNamesRequestDTO {

    // Single skill per request — frontend sends one skill at a time
    private String skill;

    // Kept for reference/logging only — NOT used in query
    // Matching is done purely by skill
    private String technology;

    // Sent by frontend — not used in logic
    private Integer permissionId;
}