package com.ikonicit.resource.tracker.dto;

import com.ikonicit.resource.tracker.utils.LeaveStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveReqUpdateStatusDTO {
    private int leaveReqId;
    private LeaveStatus status;
}
