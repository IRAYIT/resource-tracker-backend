package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ProjectRoleDTO {
    private Integer id;
    private String roleLabel;
    private String technology;
    private List<String> skills;
    private List<Integer> resourceIds;
    private List<String> resourceNames;

}