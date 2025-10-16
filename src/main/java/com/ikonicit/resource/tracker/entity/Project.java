package com.ikonicit.resource.tracker.entity;

import java.io.Serializable;
import java.util.Date;


import jakarta.persistence.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Parasuram
 */
@Getter
@Setter
@Entity
@Component
@Scope(scopeName = "prototype")
@Table(name = "projects")
public class Project implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    @JsonBackReference
    private Resource resource;
    private String name;
    @Column(name = "client_name")
    private String clientName;
    @Lob
    private byte[] attachment;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    private String technology;
    private String skills;
    private String status;
    private String amount;
    @Column(name = "developer_amount")
    private String developerAmount;
    @Column(name = "total_amount")
    private String totalAmount;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_at")
    private Date updatedAt;
    @Column(name = "updated_by")
    private String updatedBy;

}
