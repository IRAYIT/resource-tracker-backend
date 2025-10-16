package com.ikonicit.resource.tracker.dto;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Component
@Scope(scopeName = "prototype")
public class OpeningsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String hours;

    private String shiftTimings;

    private String payment;

    private String paymentType;

    private String technology;

    private String skill;

    private BigDecimal experience;

    private String employmentType;

    private String startDate;

    private String endDate;

    private String status;

    private Date createdAt;

    private String createdBy;

    private Date updatedAt;

    private String updatedBy;
}
