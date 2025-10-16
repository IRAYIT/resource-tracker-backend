package com.ikonicit.resource.tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "credentials")
@Component
@Scope(scopeName = "prototype")
public class Credentials implements Serializable {

    public static  final long serialVersionUID = 1L;

    @Id
    @Column(name = "resource_id")
    private Integer id;
 
 
    @OneToOne(fetch =FetchType.LAZY )
    @MapsId
    @JoinColumn(name = "resource_id")
    private Resource resource;
   
    @Column(name = "email")
    private String email;
    private String password;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_at")
    private Date updatedAt;
    @Column(name = "updated_by")
    private String updatedBy;}
