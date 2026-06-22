package com.ikonicit.resource.tracker.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "project_role")
public class ProjectRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_label")
    private String roleLabel;         // Backend, Frontend, Testing etc.

    @Column(name = "technology")
    private String technology;        // JAVA, REACTJS etc.

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;            // stored as comma-separated: "Spring Boot,Hibernate"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;          // belongs to which project

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "project_role_resource",
            joinColumns = @JoinColumn(name = "project_role_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private List<Resource> resources; // assigned developers for this role
}
