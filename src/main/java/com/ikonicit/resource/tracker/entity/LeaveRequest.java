package com.ikonicit.resource.tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Entity
@Component
@Data
@Scope(scopeName = "prototype")
@Table(name = "leave")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    private String email;
    private String department;
    private String date;
    private String typeOfAbsence;
    private Date absenceFrom;
    private Date absenceTo;
    private Integer totalDays;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;
}
