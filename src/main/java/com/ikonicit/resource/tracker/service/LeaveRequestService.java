package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.*;

import java.util.List;

public interface LeaveRequestService {

    LeaveResponseDTO createLeaveRequest(LeaveRequestDTO leaveRequestDTO);
    LeaveResponseDTO updateLeaveRequest(LeaveRequestDTO leaveRequestDTO);
    DeletedLeaveResponseDTO deleteLeaveRequest(Integer id);

    LeaveResponseDTO getLeaveRequestById(Integer id);

    int getTotalDays(Integer id);

    List<LeaveResponseDTO> getAll();

    List<LeaveResponseDTO> getLeaveRequestByResourceId(Integer resourceId);

    List<LeaveResponseDTO> getLeaveRequestBymanagerId(Integer managerId);

    List<LeaveTypeDTO> getLeaveTypes();

    String updateStatus(LeaveReqUpdateStatusDTO leaveReqUpdateStatusDTO);

}