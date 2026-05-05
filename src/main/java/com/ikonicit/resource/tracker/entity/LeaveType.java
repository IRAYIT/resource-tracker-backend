package com.ikonicit.resource.tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GeneratedColumn;

@Entity
@Data
@Table(name = "leave_type")
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String type;
    private int maxDaysPerYear;

}
