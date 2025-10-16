package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class EmployeeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String employeeName;
    private String email;
    private Date hireDate;
    private EmployeeDTO manager;
    private PermissionDTO permission;
    private String status;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;


}
