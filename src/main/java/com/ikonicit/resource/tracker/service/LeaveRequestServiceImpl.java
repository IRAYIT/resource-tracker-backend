package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.*;
import com.ikonicit.resource.tracker.entity.LeaveRequest;
import com.ikonicit.resource.tracker.entity.LeaveType;
import com.ikonicit.resource.tracker.entity.Resource;
import com.ikonicit.resource.tracker.exception.BadRequestException;
import com.ikonicit.resource.tracker.repository.LeaveRequestRepository;
import com.ikonicit.resource.tracker.repository.LeaveTypeRepository;
import com.ikonicit.resource.tracker.repository.ResourceRepository;
import com.ikonicit.resource.tracker.utils.LeaveStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.ikonicit.resource.tracker.predicates.Predicates.isNotNull;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Transactional
    public LeaveResponseDTO createLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        LeaveType leaveType = leaveTypeRepository.findById(leaveRequestDTO.getLeaveTypeId())
                .orElseThrow(() -> new BadRequestException("Leave Type is Not Valid"));

        Resource resource = resourceRepository.findById(leaveRequestDTO.getResourceId())
                .orElseThrow(() -> new BadRequestException("Invalid resource"));

        LeaveRequest leaveRequest = new LeaveRequest();
        BeanUtils.copyProperties(leaveRequestDTO, leaveRequest);
        if (leaveRequest.getLeaveStatus() == null) {
            leaveRequest.setLeaveStatus(LeaveStatus.PENDING);
        }
        leaveRequest.setTotalDays(calculateDaysBetweenDates(
                leaveRequestDTO.getAbsenceFrom(), leaveRequestDTO.getAbsenceTo()));
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setResource(resource);
        leaveRequest.setEmail(resource.getEmail());
        leaveRequest.setDate(new Date());
        LeaveRequest savedReq = leaveRequestRepository.save(leaveRequest);

        LeaveResponseDTO leaveResponseDTO = new LeaveResponseDTO();
        leaveResponseDTO.setId(savedReq.getId());
        BeanUtils.copyProperties(leaveRequest, leaveResponseDTO);
        leaveResponseDTO.setResourceId(resource.getId());
        LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO();
        BeanUtils.copyProperties(savedReq.getLeaveType(), leaveTypeDTO);
        leaveResponseDTO.setLeaveTypeDTO(leaveTypeDTO);
        if(savedReq.getResource().getManager() != null)
            leaveResponseDTO.setManagerId(savedReq.getResource().getManager().getId());
        return leaveResponseDTO;
    }

    @Transactional
    public LeaveResponseDTO updateLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        if(leaveRequestDTO.getId() == null){
            throw new BadRequestException("LeaveRequest Id is required");
        }

        LeaveRequest existingLeaveRequest = leaveRequestRepository.findById(leaveRequestDTO.getId())
                .orElseThrow(() -> new BadRequestException("LeaveRequest not found"));

        if (leaveRequestDTO.getResourceId() != null) {
            Resource resource = resourceRepository.findById(leaveRequestDTO.getResourceId())
                    .orElseThrow(() -> new BadRequestException("Invalid resource"));
            existingLeaveRequest.setResource(resource);
            existingLeaveRequest.setEmail(resource.getEmail());
        }

        if (leaveRequestDTO.getLeaveTypeId() != null) {
            LeaveType leaveType = leaveTypeRepository.findById(leaveRequestDTO.getLeaveTypeId())
                    .orElseThrow(() -> new BadRequestException("Invalid Leave Type"));
            existingLeaveRequest.setLeaveType(leaveType);
        }

        existingLeaveRequest.setAbsenceFrom(leaveRequestDTO.getAbsenceFrom());
        existingLeaveRequest.setAbsenceTo(leaveRequestDTO.getAbsenceTo());
        existingLeaveRequest.setReason(leaveRequestDTO.getReason());
        existingLeaveRequest.setTotalDays(calculateDaysBetweenDates(
                leaveRequestDTO.getAbsenceFrom(), leaveRequestDTO.getAbsenceTo()));

        LeaveRequest updatedLeaveRequest = leaveRequestRepository.save(existingLeaveRequest);

        LeaveResponseDTO leaveResponseDTO = new LeaveResponseDTO();
        leaveResponseDTO.setId(updatedLeaveRequest.getId());
        BeanUtils.copyProperties(updatedLeaveRequest, leaveResponseDTO);
        leaveResponseDTO.setResourceId(updatedLeaveRequest.getResource().getId());
        LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO();
        BeanUtils.copyProperties(updatedLeaveRequest.getLeaveType(), leaveTypeDTO);
        leaveResponseDTO.setLeaveTypeDTO(leaveTypeDTO);
        if(updatedLeaveRequest.getResource().getManager() != null)
            leaveResponseDTO.setManagerId(updatedLeaveRequest.getResource().getManager().getId());
        return leaveResponseDTO;

    }


    public DeletedLeaveResponseDTO deleteLeaveRequest(Integer id) {
        DeletedLeaveResponseDTO deletedLeaveRequestDTO = new DeletedLeaveResponseDTO();
        Optional<LeaveRequest> leaveRequestOptional = leaveRequestRepository.findById(id);
        if(!leaveRequestOptional.isPresent()){
            throw new BadRequestException("Invalid LeaveRequest ID");
        }
        deletedLeaveRequestDTO.setId(id);
        leaveRequestRepository.deleteById(id);
        deletedLeaveRequestDTO.setStatus("Deleted Leave Request");
        deletedLeaveRequestDTO.setMessage("Deleted Leave Request Successfully");
        return deletedLeaveRequestDTO;
    }

    public LeaveResponseDTO getLeaveRequestById(Integer id) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Leave request with id " + id + " not found"));

        LeaveResponseDTO leaveResponseDTO = new LeaveResponseDTO();
        BeanUtils.copyProperties(leaveRequest, leaveResponseDTO);
        LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO();
        BeanUtils.copyProperties(leaveRequest.getLeaveType(), leaveTypeDTO);
        leaveResponseDTO.setLeaveTypeDTO(leaveTypeDTO);
        if(leaveRequest.getResource() != null) {
            leaveResponseDTO.setResourceId(leaveRequest.getResource().getId());
            leaveResponseDTO.setEmployeeName(leaveRequest.getResource().getFirstName() +" "+  leaveRequest.getResource().getLastName());
            if (leaveRequest.getResource().getManager() != null)
                leaveResponseDTO.setManagerId(leaveRequest.getResource().getManager().getId());
        }

        return leaveResponseDTO;
    }

    public int getTotalDays(Integer id) {


        LeaveRequest leaveRequest = leaveRequestRepository.findById(id).orElse(null);
        if (leaveRequest != null) {
            return calculateDaysBetweenDates(leaveRequest.getAbsenceFrom(), leaveRequest.getAbsenceTo());
        }
        return 0;
    }

    @Override
    public List<LeaveResponseDTO> getAll() {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
        List<LeaveResponseDTO> leaveResponseDTOList = new ArrayList<>();

        for(LeaveRequest leaveRequest : leaveRequests){

            LeaveResponseDTO leaveResponseDTO = new LeaveResponseDTO();
            BeanUtils.copyProperties(leaveRequest, leaveResponseDTO);

            LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO();
            BeanUtils.copyProperties(leaveRequest.getLeaveType(), leaveTypeDTO);
            leaveResponseDTO.setLeaveTypeDTO(leaveTypeDTO);

            if(leaveRequest.getResource() != null) {
                leaveResponseDTO.setResourceId(leaveRequest.getResource().getId());
                leaveResponseDTO.setEmployeeName(leaveRequest.getResource().getFirstName() +" "+  leaveRequest.getResource().getLastName());
                if (leaveRequest.getResource().getManager() != null)
                    leaveResponseDTO.setManagerId(leaveRequest.getResource().getManager().getId());
            }

            leaveResponseDTOList.add(leaveResponseDTO);
        }

        return leaveResponseDTOList;
    }

    @Override
    public List<LeaveResponseDTO> getLeaveRequestByResourceId(Integer resourceId) {

        List<LeaveRequest> leaveRequests= leaveRequestRepository.findByResourceId(resourceId);

        List<LeaveResponseDTO> leaveResponseDTOList = new ArrayList<>();

        for(LeaveRequest leaveRequest : leaveRequests){
            LeaveResponseDTO leaveResponseDTO = new LeaveResponseDTO();
            BeanUtils.copyProperties(leaveRequest, leaveResponseDTO);

            LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO();
            BeanUtils.copyProperties(leaveRequest.getLeaveType(), leaveTypeDTO);
            leaveResponseDTO.setLeaveTypeDTO(leaveTypeDTO);

            if(leaveRequest.getResource() != null) {
                leaveResponseDTO.setResourceId(leaveRequest.getResource().getId());
                leaveResponseDTO.setEmployeeName(leaveRequest.getResource().getFirstName() +" "+  leaveRequest.getResource().getLastName());
                if (leaveRequest.getResource().getManager() != null)
                    leaveResponseDTO.setManagerId(leaveRequest.getResource().getManager().getId());
            }

            leaveResponseDTOList.add(leaveResponseDTO);
        }

         return leaveResponseDTOList ;
    }

    @Override
    public List<LeaveResponseDTO> getLeaveRequestBymanagerId(Integer managerId) {
        List<LeaveRequest> leaveRequests= leaveRequestRepository
                .findByResource_ManagerIdAndResource_StatusNot(managerId, "TERMINATED");

        List<LeaveResponseDTO> leaveResponseDTOList = new ArrayList<>();

        for(LeaveRequest leaveRequest : leaveRequests){
            LeaveResponseDTO leaveResponseDTO = new LeaveResponseDTO();
            BeanUtils.copyProperties(leaveRequest, leaveResponseDTO);

            LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO();
            BeanUtils.copyProperties(leaveRequest.getLeaveType(), leaveTypeDTO);
            leaveResponseDTO.setLeaveTypeDTO(leaveTypeDTO);

            if(leaveRequest.getResource() != null) {
                leaveResponseDTO.setResourceId(leaveRequest.getResource().getId());
                leaveResponseDTO.setEmployeeName(leaveRequest.getResource().getFirstName() +" "+  leaveRequest.getResource().getLastName());
                if (leaveRequest.getResource().getManager() != null)
                    leaveResponseDTO.setManagerId(leaveRequest.getResource().getManager().getId());
            }

            leaveResponseDTOList.add(leaveResponseDTO);
        }
        return leaveResponseDTOList;
    }

    private int calculateDaysBetweenDates(Date fromDate, Date toDate) {
        LocalDate startDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if(endDate.isBefore(startDate)) throw new BadRequestException("End date must be after start date");
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return (int) daysBetween;
    }

    @Override
    public List<LeaveTypeDTO> getLeaveTypes() {
        ArrayList<LeaveTypeDTO> leaveTypeDTOlist = new ArrayList<>();

        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        leaveTypes.forEach(leaveType -> {
            LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO();
            BeanUtils.copyProperties(leaveType, leaveTypeDTO);
            leaveTypeDTOlist.add(leaveTypeDTO);
        });

        return leaveTypeDTOlist;
    }

    @Transactional
    public String updateStatus(LeaveReqUpdateStatusDTO leaveReqUpdateStatusDTO){

        LeaveRequest leaveRequest = leaveRequestRepository
                .findById(leaveReqUpdateStatusDTO.getLeaveReqId())
                .orElseThrow(() -> new BadRequestException("Leave Request Not Found"));

        if(leaveRequest.getLeaveStatus() != LeaveStatus.PENDING){
            throw new BadRequestException("Leave status cannot be updated once it is "+ leaveRequest.getLeaveStatus());
        }

        LeaveStatus newLeaveStatus;
        try{
         newLeaveStatus = LeaveStatus.valueOf(leaveReqUpdateStatusDTO.getStatus().name());}
        catch(IllegalArgumentException ex){
            throw new BadRequestException("Invalid Leave Status : "+ leaveReqUpdateStatusDTO.getStatus());
        }

        leaveRequest.setLeaveStatus(newLeaveStatus);
        leaveRequestRepository.save(leaveRequest);

        return "Leave Status Updated Successfully";
    }

    

}
