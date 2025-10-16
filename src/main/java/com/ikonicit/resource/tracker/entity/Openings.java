package com.ikonicit.resource.tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Ram
 */

@Data
@Entity
@Table(name = "openings")
@Component
@Scope(scopeName = "prototype")
public class Openings implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String hours;

    @Column(name = "shift_timings")
    private String shiftTimings;

    private String payment;

    private String paymentType;

    private String technology;

    private String skill;

    private BigDecimal experience;

    @Column(name = "employementtype")
    private String employmentType;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    private String status;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;


}


