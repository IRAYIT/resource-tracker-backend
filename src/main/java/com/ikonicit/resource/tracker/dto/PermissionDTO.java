package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@Scope(scopeName = "prototype")
public class PermissionDTO {


    private Integer id;
    private String permissionName;
    private String permissionKey;

}
