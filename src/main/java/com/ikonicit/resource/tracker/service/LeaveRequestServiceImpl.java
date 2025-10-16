package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.LeaveRequestDTO;
import com.ikonicit.resource.tracker.dto.ResourceDTO;
import com.ikonicit.resource.tracker.entity.LeaveRequest;
import com.ikonicit.resource.tracker.entity.Resource;
import com.ikonicit.resource.tracker.exception.ResourceNotFoundException;
import com.ikonicit.resource.tracker.repository.LeaveRequestRepository;
import com.ikonicit.resource.tracker.repository.ResourceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ResourceRepository resourceRepository;

    public LeaveRequestDTO createLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        LeaveRequest leaveRequest = new LeaveRequest();
        BeanUtils.copyProperties(leaveRequestDTO, leaveRequest);
        leaveRequest.setTotalDays(calculateDaysBetweenDates(
                leaveRequestDTO.getAbsenceFrom(),leaveRequestDTO.getAbsenceTo()));
        Resource resource = new Resource();
        BeanUtils.copyProperties(leaveRequestDTO.getResourceDTO(), resource);
        leaveRequest.setEmail(resource.getEmail());
        leaveRequest.setResource(resource);
        leaveRequestRepository.save(leaveRequest);
     return leaveRequestDTO;

    }


    public LeaveRequestDTO updateLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        LeaveRequest leaveRequest = new LeaveRequest();
        BeanUtils.copyProperties(leaveRequestDTO, leaveRequest);
        leaveRequest.setTotalDays(calculateDaysBetweenDates(
                leaveRequestDTO.getAbsenceFrom(),leaveRequestDTO.getAbsenceTo()));
        Resource resource = new Resource();
        BeanUtils.copyProperties(leaveRequestDTO.getResourceDTO(), resource);
        leaveRequest.setEmail(resource.getEmail());
        leaveRequest.setResource(resource);
        leaveRequestRepository.save(leaveRequest);
        return leaveRequestDTO;

    }


    public void deleteLeaveRequest(Integer id) {
        leaveRequestRepository.deleteById(id);
    }

    public LeaveRequestDTO getLeaveRequestById(Integer id) {
        LeaveRequest leaveRequest = null;
        LeaveRequestDTO leaveRequestDTO = new LeaveRequestDTO();

        Optional<LeaveRequest> leaveRequestOptional = leaveRequestRepository.findById(id);
        if (isNotNull.test(leaveRequestOptional) && leaveRequestOptional.isPresent()) {
            leaveRequest = leaveRequestOptional.get();
            Resource resource=new Resource();
            BeanUtils.copyProperties(leaveRequest.getResource(),resource);

            leaveRequest.setEmail(resource.getEmail());


            ResourceDTO resourceDTO = new ResourceDTO();
            BeanUtils.copyProperties(resource, resourceDTO);
            resourceDTO.setPermissionId(resource.getPermission().getId());
            resourceDTO.setManagerId(resource.getManager().getId());

            leaveRequestDTO.setResourceDTO(resourceDTO);
            BeanUtils.copyProperties(leaveRequest, leaveRequestDTO);
        } else {
            throw new ResourceNotFoundException("Leave request with id not found");
        }
        return leaveRequestDTO;
    }

    public int getTotalDays(Integer id) {


        LeaveRequest leaveRequest = leaveRequestRepository.findById(id).orElse(null);
        if (leaveRequest != null) {
            return calculateDaysBetweenDates(leaveRequest.getAbsenceFrom(), leaveRequest.getAbsenceTo());
        }
        return 0;
    }

    @Override
    public List<LeaveRequestDTO> getAll() {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
        List<LeaveRequestDTO> leaveRequestDTOList = new ArrayList<>();

        for (LeaveRequest leaveRequest : leaveRequests) {
            LeaveRequestDTO leaveRequestDTO = new LeaveRequestDTO();

            if (leaveRequest.getResource() != null) {
                Resource resource = new Resource();
                BeanUtils.copyProperties(leaveRequest.getResource(), resource);

                ResourceDTO resourceDTO = new ResourceDTO();
                BeanUtils.copyProperties(resource, resourceDTO);

                if (resource.getPermission() != null) {
                    resourceDTO.setPermissionId(resource.getPermission().getId());
                }

                if (resource.getManager() != null) {
                    resourceDTO.setManagerId(resource.getManager().getId());
                }

                leaveRequestDTO.setResourceDTO(resourceDTO);
            }

            BeanUtils.copyProperties(leaveRequest, leaveRequestDTO);
            leaveRequestDTOList.add(leaveRequestDTO);
        }

        return leaveRequestDTOList;
    }

    @Override
    public List<LeaveRequestDTO> getLeaveRequestByResourceId(Integer resourceId) {

        List<LeaveRequest> leaveRequests= leaveRequestRepository.findByResourceId(resourceId);
        List<LeaveRequestDTO> leaveRequestDTOList = new ArrayList<>();
        for (LeaveRequest leaveRequest : leaveRequests) {
            Resource resource=new Resource();
            BeanUtils.copyProperties(leaveRequest.getResource(),resource);
            LeaveRequestDTO leaveRequestDTO = new LeaveRequestDTO();
            BeanUtils.copyProperties(leaveRequest, leaveRequestDTO);
            ResourceDTO resourceDTO = new ResourceDTO();
            BeanUtils.copyProperties(resource, resourceDTO);
            resourceDTO.setPermissionId(resource.getPermission().getId());
            resourceDTO.setManagerId(resource.getManager().getId());
            leaveRequestDTO.setResourceDTO(resourceDTO);
            leaveRequestDTOList.add(leaveRequestDTO);
        };
         return leaveRequestDTOList  ;
    }
//
    @Override
    public List<LeaveRequestDTO> getLeaveRequestBymanagerId(Integer managerId) {
        List<LeaveRequest> leaveRequests= leaveRequestRepository.findByResource_ManagerId(managerId);
        List<LeaveRequestDTO> leaveRequestDTOList = new ArrayList<>();

        for (LeaveRequest leaveRequest : leaveRequests) {

            Resource resource=new Resource();
            BeanUtils.copyProperties(leaveRequest.getResource(),resource);
            leaveRequest.setEmail(resource.getEmail());

            LeaveRequestDTO leaveRequestDTO = new LeaveRequestDTO();
            BeanUtils.copyProperties(leaveRequest, leaveRequestDTO);
            ResourceDTO resourceDTO=new ResourceDTO();
            BeanUtils.copyProperties(resource, resourceDTO);
            resourceDTO.setPermissionId(resource.getPermission().getId());
            resourceDTO.setManagerId(resource.getManager().getId());

            leaveRequestDTO.setResourceDTO(resourceDTO);
            leaveRequestDTOList.add(leaveRequestDTO);
        }
        return leaveRequestDTOList;
    }

    private int calculateDaysBetweenDates(Date fromDate, Date toDate) {
        LocalDate startDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return Math.toIntExact(Math.abs(daysBetween));
    }
//    private List<LeaveRequestDTO> buildLeaveRequestDTOList(List<LeaveRequest> leaveRequests) {
//        List<LeaveRequestDTO> leaveRequestDTOS = new ArrayList<>();
//        leaveRequests.forEach( leaveRequest-> {
//            leaveRequestDTOS.add(buildLeaveRequestDTO(leaveRequest));
//        });
//        return leaveRequestDTOS;
//    }
//    private List<LeaveRequestDTO> buildLeaveRequestDTOSList(List<LeaveRequest> leaveRequests) {
//        List<LeaveRequestDTO> leaveRequestDTOS = new ArrayList<>();
//        leaveRequests.forEach(leaveRequest -> {
//            leaveRequestDTOS.add(buildLeaveRequestDTO(leaveRequest));
//        });
//        return leaveRequestDTOS;
//    }
//
////    private LeaveRequest buildLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
////        LeaveRequest leaveRequest = new LeaveRequest();
////        Resource resource = new Resource();
////        BeanUtils.copyProperties(leaveRequestDTO.getResourceDTO(),resource);
////        resource.setId(leaveRequestDTO.getResourceId());
////        BeanUtils.copyProperties(leaveRequestDTO,leaveRequest);
////        leaveRequest.setEmail(resource.getEmail());
////        return leaveRequest;
////    }
////
/////

}
