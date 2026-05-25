package com.ikonicit.resource.tracker.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

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

    private String name;

    @Column(name = "client_name")
    private String clientName;

    @Column(columnDefinition = "bytea")
    private byte[] attachment;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

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

    // ✅ Replaces old @ManyToMany resources + flat technology/skills fields
    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<ProjectRole> projectRoles;
}