package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Parasuram
 */
@Getter
@Setter
@Component
@Scope(scopeName = "prototype")
public class ProjectDTO implements Serializable {
    private Integer id;
    private String name;
    private String clientName;
    private Date startDate;
    private Date endDate;
    private String status;
//    private String amount;
//    private String developerAmount;
//    private String totalAmount;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;
    private List<ProjectRoleDTO> projectRoles;


}
