package com.ikonicit.resource.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope(scopeName = "prototype")
public class ResourceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer managerId;
    private Integer permissionId;
    private String resourceName;
    private String firstName;
    private String lastName;
    private String email;
    private String linkedin;
    private String phone;
    private String technology;
    private String employmentType;
    private Date startDate;
    private Date endDate;
    private String status;
    private String skill;
    private BigDecimal experience;
    private String comments;
    private ResourceDTO manager;
    private PermissionDTO permission;
    private List<ResourceAttachmentDTO> resourceAttachments;
   // private List<ProjectDTO> projects;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;
    private Boolean client;
    private String resourceType;
    private List<Integer> assignedResourceIds;

}
