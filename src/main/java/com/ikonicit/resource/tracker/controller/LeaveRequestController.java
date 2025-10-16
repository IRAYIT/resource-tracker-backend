package com.ikonicit.resource.tracker.controller;

import com.ikonicit.resource.tracker.dto.LeaveRequestDTO;
import com.ikonicit.resource.tracker.service.LeaveRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/leave")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<LeaveRequestDTO> createLeaveRequest(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        return new ResponseEntity<>(leaveRequestService.createLeaveRequest(leaveRequestDTO), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<LeaveRequestDTO> updateLeaveRequest(
            @RequestBody LeaveRequestDTO leaveRequestDTO) {
        return new ResponseEntity<>(leaveRequestService.updateLeaveRequest(leaveRequestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Integer id) {
        leaveRequestService.deleteLeaveRequest(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getLeaveRequestById(@PathVariable Integer id) {
        LeaveRequestDTO leaveRequestDTO = leaveRequestService.getLeaveRequestById(id);
        return leaveRequestDTO != null ?
                new ResponseEntity<>(leaveRequestDTO, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
//
    @GetMapping("/{id}/total-days")
    public ResponseEntity<Integer> getTotalDays(@PathVariable Integer id) {
        int totalDays = leaveRequestService.getTotalDays(id);
        return new ResponseEntity<>(totalDays, HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<List<LeaveRequestDTO>> getAllLeaveRequest() {
        List<LeaveRequestDTO> leaveRequestDTOS = leaveRequestService.getAll();
        return new ResponseEntity<>(leaveRequestDTOS, HttpStatus.OK);
    }

    @GetMapping("all/{resourceId}")
    public ResponseEntity<List<LeaveRequestDTO>> getLeaveRequestByResourceId(@PathVariable Integer resourceId) {
        List<LeaveRequestDTO> leaveRequestDTOS = leaveRequestService.getLeaveRequestByResourceId(resourceId);
        return new ResponseEntity<>(leaveRequestDTOS, HttpStatus.OK);
    }

    @GetMapping("all/manager/{managerId}")
    public ResponseEntity<List<LeaveRequestDTO>> getLeaveRequestByManagerId(@PathVariable Integer managerId) {
        List<LeaveRequestDTO> leaveRequestDTOS = leaveRequestService.getLeaveRequestBymanagerId(managerId);
        return new ResponseEntity<>(leaveRequestDTOS, HttpStatus.OK);
    }
}
