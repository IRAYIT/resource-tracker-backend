package com.ikonicit.resource.tracker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ikonicit.resource.tracker.utils.LeaveStatus;
import jakarta.persistence.*;
import lombok.Data;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Entity
@Component
@Data
@Scope(scopeName = "prototype")
@Table(name = "leave_request")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    private String email;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;
    private Date absenceFrom;
    private Date absenceTo;
    private String reason;
    private Integer totalDays;
    @Enumerated(EnumType.STRING)
    private LeaveStatus leaveStatus = LeaveStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    @JsonBackReference
    private Resource resource;
}
