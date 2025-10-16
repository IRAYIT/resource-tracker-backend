package com.ikonicit.resource.tracker.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Ram
 */

@Data
@Entity
@Table(name = "resource")
@Component
@Scope(scopeName = "prototype")
public class Resource implements Serializable {
    public Resource() {}
    public Resource(Integer id) {
        this.id = id;
    }

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    private String linkedin;

    private String comments;

    private String phone;

    private String technology;

    @Column(name = "employementtype")
    private String employmentType;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    private String status;

    private String skill;

    private BigDecimal experience;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", referencedColumnName = "id", nullable = true)
    private Resource manager;
    @OneToOne(mappedBy = "resource", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Credentials credentials;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "resource")
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    private List<ResourceAttachments> attachments;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "resource")
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    private List<Project> projects;

    @OneToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name = "permission_id")
    @JoinColumn(name = "permission_id",unique = false)
    private Permission permission;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "resource")
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    private List<LeaveRequest> leaveRequests;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "client")
    private Boolean client;

    @Column(name="resourceType")
    private String resourceType;



}


