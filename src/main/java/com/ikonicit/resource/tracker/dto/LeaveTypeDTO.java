package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveTypeDTO {
    private int id;
    private String type;
    private int maxDaysPerYear;
}
