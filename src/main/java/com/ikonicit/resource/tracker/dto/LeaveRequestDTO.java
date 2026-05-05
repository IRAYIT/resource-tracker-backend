package com.ikonicit.resource.tracker.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ikonicit.resource.tracker.entity.LeaveType;
import com.ikonicit.resource.tracker.entity.Resource;
import com.ikonicit.resource.tracker.utils.LeaveStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
@Getter
@Setter
public class LeaveRequestDTO {

    private Integer id;
    private Integer leaveTypeId;
    private Date absenceFrom;
    private Date absenceTo;
    private String reason;
    private Integer resourceId;
}
