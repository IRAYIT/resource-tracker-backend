package com.ikonicit.resource.tracker.controller;

import com.ikonicit.resource.tracker.dto.*;
import com.ikonicit.resource.tracker.service.LeaveRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("api/v1/leave")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<LeaveResponseDTO> createLeaveRequest(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        return new ResponseEntity<>(leaveRequestService.createLeaveRequest(leaveRequestDTO), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<LeaveResponseDTO> updateLeaveRequest(
            @RequestBody LeaveRequestDTO leaveRequestDTO) {
        return new ResponseEntity<>(leaveRequestService.updateLeaveRequest(leaveRequestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedLeaveResponseDTO> deleteLeaveRequest(@PathVariable Integer id) {
        return ResponseEntity.ok(leaveRequestService.deleteLeaveRequest(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponseDTO> getLeaveRequestById(@PathVariable Integer id) {
        LeaveResponseDTO leaveResponseDTO = leaveRequestService.getLeaveRequestById(id);
        return leaveResponseDTO != null ?
                new ResponseEntity<>(leaveResponseDTO, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}/total-days")
    public ResponseEntity<Integer> getTotalDays(@PathVariable Integer id) {
        int totalDays = leaveRequestService.getTotalDays(id);
        return new ResponseEntity<>(totalDays, HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<List<LeaveResponseDTO>> getAllLeaveRequest() {
        List<LeaveResponseDTO> leaveResponseDTOS = leaveRequestService.getAll();
        return new ResponseEntity<>(leaveResponseDTOS, HttpStatus.OK);
    }

    @GetMapping("all/{resourceId}")
    public ResponseEntity<List<LeaveResponseDTO>> getLeaveRequestByResourceId(@PathVariable Integer resourceId) {
        List<LeaveResponseDTO> leaveResponseDTOS = leaveRequestService.getLeaveRequestByResourceId(resourceId);
        return new ResponseEntity<>(leaveResponseDTOS, HttpStatus.OK);
    }

    @GetMapping("all/manager/{managerId}")
    public ResponseEntity<List<LeaveResponseDTO>> getLeaveRequestByManagerId(@PathVariable Integer managerId) {
        List<LeaveResponseDTO> leaveResponseDTOS = leaveRequestService.getLeaveRequestBymanagerId(managerId);
        return new ResponseEntity<>(leaveResponseDTOS, HttpStatus.OK);
    }

    @GetMapping("/leaveTypes")
    public ResponseEntity<List<LeaveTypeDTO>> getLeaveTypes() {
        List<LeaveTypeDTO> leaveTypeDTOList = leaveRequestService.getLeaveTypes();
        return new ResponseEntity<>(leaveTypeDTOList, HttpStatus.OK);
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<String> updateStatus(@RequestBody LeaveReqUpdateStatusDTO leaveReqUpdateStatusDTO){
        return ResponseEntity.ok(leaveRequestService.updateStatus(leaveReqUpdateStatusDTO));
    }
}
