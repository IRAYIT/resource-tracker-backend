package com.ikonicit.resource.tracker.dto;

import com.ikonicit.resource.tracker.utils.LeaveStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class LeaveResponseDTO {
    private Integer id;
    private String email;
    private LeaveTypeDTO leaveTypeDTO;
    private Integer resourceId;
    private String employeeName;
    private Date absenceFrom;
    private Date absenceTo;
    private Integer totalDays;
    private LeaveStatus leaveStatus;
    private Integer managerId;
    private String reason;

}
