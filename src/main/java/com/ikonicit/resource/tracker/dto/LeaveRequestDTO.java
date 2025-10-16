package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
@Getter
@Setter
@Component
@Scope(scopeName = "prototype")
public class LeaveRequestDTO {

    private Integer id;
    private String email;
    private String department;
    private String date;
    private String typeOfAbsence;
    private Date absenceFrom;
    private Date absenceTo;
    private Integer totalDays;
    private ResourceDTO resourceDTO;
}
