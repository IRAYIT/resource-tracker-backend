package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.LeaveRequestDTO;
import com.ikonicit.resource.tracker.entity.LeaveRequest;

import java.util.List;

public interface LeaveRequestService {

    LeaveRequestDTO createLeaveRequest(LeaveRequestDTO leaveRequestDTO);
    LeaveRequestDTO updateLeaveRequest(LeaveRequestDTO leaveRequestDTO);
    void deleteLeaveRequest(Integer id);

    LeaveRequestDTO getLeaveRequestById(Integer id);

    int getTotalDays(Integer id);

    List<LeaveRequestDTO> getAll();

    List<LeaveRequestDTO> getLeaveRequestByResourceId(Integer resourceId);

    List<LeaveRequestDTO> getLeaveRequestBymanagerId(Integer managerId);
}