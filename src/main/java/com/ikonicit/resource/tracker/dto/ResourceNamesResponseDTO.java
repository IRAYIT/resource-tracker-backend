package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNamesResponseDTO {

    private Integer id;
    private String resourceName;

    public ResourceNamesResponseDTO(Integer id, String resourceName) {
        this.id = id;
        this.resourceName = resourceName;
    }
}
