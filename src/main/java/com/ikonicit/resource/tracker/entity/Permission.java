package com.ikonicit.resource.tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "permission")
@Component
@Scope(scopeName = "prototype")
public class Permission implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @Column(name = "permission_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "permission_name")
    private String permissionName;
    @Column(name = "permission_key")
    private String permissionKey;

}
