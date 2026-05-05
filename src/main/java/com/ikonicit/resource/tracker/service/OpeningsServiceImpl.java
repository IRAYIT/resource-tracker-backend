package com.ikonicit.resource.tracker.service;

import com.google.gson.Gson;
import com.ikonicit.resource.tracker.dto.OpeningsDTO;
import com.ikonicit.resource.tracker.dto.OpeningsResponseDTO;
import com.ikonicit.resource.tracker.entity.Openings;
import com.ikonicit.resource.tracker.entity.Resource;
import com.ikonicit.resource.tracker.exception.BadRequestException;
import com.ikonicit.resource.tracker.exception.MailSendFailedException;
import com.ikonicit.resource.tracker.exception.ResourceNotFoundException;
import com.ikonicit.resource.tracker.predicates.Predicates;
import com.ikonicit.resource.tracker.repository.CandidateRepository;
import com.ikonicit.resource.tracker.repository.OpeningsRepository;
import com.ikonicit.resource.tracker.repository.ResourceRepository;
import com.ikonicit.resource.tracker.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author Parasuram
 * @since 10-03-2021
 */
@Slf4j
@Service
public class OpeningsServiceImpl implements OpeningsService {

    private OpeningsRepository openingsRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private CandidateRepository candidateRepository;

    Predicate<Object> isNotNull = Predicates.isNotNull;

    @Value("${spring.openingsEmailEnabled}")
    private Boolean openingsEmailEnabled;

    public OpeningsServiceImpl(OpeningsRepository openingsRepository) {
        this.openingsRepository = openingsRepository;
        log.info("object created for repository {}", openingsRepository);
    }

    @Override
    public OpeningsResponseDTO create(OpeningsDTO openingsDTO) {

        Openings openings = buildOpenings(openingsDTO);

        // ✅ FIX: manually set relationships
        Resource createdBy = resourceRepository.findById(openingsDTO.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("HR not found"));

        Resource updatedBy = resourceRepository.findById(openingsDTO.getUpdatedBy())
                .orElseThrow(() -> new RuntimeException("HR not found"));

        openings.setCreatedBy(createdBy);
        openings.setUpdatedBy(updatedBy);

        // generate public key
        openings.setPublicUrlKey(generatePublicKey());

        openings = openingsRepository.save(openings);

        if (openingsEmailEnabled) {
           newOpeningEmail(openings);
        }

        return buildOpeningsDTO(openings);
    }

    @Override
    public OpeningsResponseDTO getOpening(Integer id) {
        Openings opening = null;
        Optional<Openings> openingsOptional = openingsRepository.findById(id);
        if (isNotNull.test(openingsOptional) && openingsOptional.isPresent()) {
            opening = openingsOptional.get();
        } else {
            throw new ResourceNotFoundException("Opening Not Found");
        }
        return buildOpeningsDTO(opening);
    }

    @Override
    public OpeningsResponseDTO update(OpeningsDTO openingsDTO) {

        if (openingsDTO.getId() == null || openingsDTO.getId() == 0) {
            throw new BadRequestException("Id is mandatory for update Opening");
        }

        Openings openings = buildOpenings(openingsDTO);

        // ✅ manually set relationships
        Resource createdBy = resourceRepository.findById(openingsDTO.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("HR not found"));

        Resource updatedBy = resourceRepository.findById(openingsDTO.getUpdatedBy())
                .orElseThrow(() -> new RuntimeException("HR not found"));

        openings.setCreatedBy(createdBy);
        openings.setUpdatedBy(updatedBy);

        return buildOpeningsDTO(openingsRepository.save(openings));
    }

    @Override
    public List<OpeningsResponseDTO> getOpenings() {
        return buildOpeningsDTOList(openingsRepository.findByStatusOrderByIdDesc(Constants.ACTIVE));
    }

    @Override
    public String deleteOpening(Integer id) {
        Optional<Openings> openingsOptional = openingsRepository.findById(id);
        if (!openingsOptional.isPresent()) {
            throw new ResourceNotFoundException("Openings Not Found in the database");
        }
        Openings openings = openingsOptional.get();
        openings.setStatus(Constants.TERMINATED);
        openingsRepository.save(openings);
        log.info("Openings Deleted Successfully");
        return "Opening Deleted Successfully";
    }

    @Override
    public List<OpeningsResponseDTO> createOpenings(List<OpeningsDTO> openingsDTO) {

        List<Openings> openings = new ArrayList<>();
        openingsDTO.forEach(dto -> {

            Openings op = buildOpenings(dto);

            Resource createdBy = resourceRepository.findById(dto.getCreatedBy())
                    .orElseThrow(() -> new RuntimeException("HR not found"));

            Resource updatedBy = resourceRepository.findById(dto.getUpdatedBy())
                    .orElseThrow(() -> new RuntimeException("HR not found"));

            op.setCreatedBy(createdBy);
            op.setUpdatedBy(updatedBy);

            openings.add(op);
        });
        return buildOpeningsDTOList(openingsRepository.saveAll(openings));
    }

    @Override
    public OpeningsResponseDTO getOpeningByPublicUrlKey(String publicUrlKey) {

        // fetch Optional once
        Openings openings = openingsRepository.findByPublicUrlKey(publicUrlKey)
                .orElseThrow(() -> new ResourceNotFoundException("Opening Not Found for the given public url key"));

        // convert entity to DTO
        return buildOpeningsDTO(openings);
    }

//    private void newOpeningEmail(OpeningsDTO openingsDTO) {
//
//        List<String> emails = resourceRepository.findEmails(Constants.TERMINATED);
//        if (!emails.isEmpty() && isNotNull.test(emails)) {
//
//            emails.forEach(email -> {
//                try {
//                    SimpleMailMessage message = new SimpleMailMessage();
//                    message.setTo(email);
////                    message.setBcc("");
//                    message.setSubject(openingsDTO.getName());
//                    message.setText("Dear Applicant's, \n\n" + openingsDTO.getName()+"(sscreativelabs.com)" + " \n" + "Technology:" + openingsDTO.getTechnology() + " \n" + "Skills:" + openingsDTO.getSkill() +" \n"
//                            + "Package:" + openingsDTO.getPayment() +" \n"+ "Salary Type:" + openingsDTO.getPaymentType()+" \n" + "Hours:" + openingsDTO.getHours() + "\n"+"ShiftTimings:" + openingsDTO.getShiftTimings()+"\n"
//                            + "Experience:" + openingsDTO.getExperience()+"\n"+"If Interested please reach us on WHATSAPP ONLY +91 8331888832 and apply in our portal:http://resourcetracker.sscreativelabs.com"+"\n\n"
//                            + "Thanks & Regards, "+"\n"+"Sunshine Creative Labs HR.");
//                    javaMailSender.send(message);
//                } catch (MailSendFailedException exception) {
//                    log.error("Exception occurred during sending an email to the applicants for Opening Updates\t{}", openingsDTO.getName());
//                    throw new MailSendFailedException("Exception occurred during sending an email to the applicant might be Email Wrong or Mail Server Down");
//                }
//            });
//        }
//    }

private List<OpeningsResponseDTO> buildOpeningsDTOList(List<Openings> openings) {
    List<OpeningsResponseDTO> openingsDTOS = new ArrayList<>();
    openings.forEach(opening -> {
        openingsDTOS.add(buildOpeningsDTO(opening));
    });
    return openingsDTOS;
}

    private Openings buildOpenings(OpeningsDTO dto) {
        dto.setStatus("ACTIVE");

        Integer createdBy = dto.getCreatedBy();
        Integer updatedBy = dto.getUpdatedBy();

        dto.setCreatedBy(null);
        dto.setUpdatedBy(null);

        Gson gson = new Gson();
        Openings openings = gson.fromJson(gson.toJson(dto), Openings.class);

        // restore (not mandatory but clean)
        dto.setCreatedBy(createdBy);
        dto.setUpdatedBy(updatedBy);

        return openings;
    }

    private OpeningsResponseDTO buildOpeningsDTO(Openings openings) {

        OpeningsResponseDTO dto = new OpeningsResponseDTO();

        dto.setId(openings.getId());
        dto.setName(openings.getName());
        dto.setHours(openings.getHours());
        dto.setShiftTimings(openings.getShiftTimings());
        dto.setPayment(openings.getPayment());
        dto.setPaymentType(openings.getPaymentType());
        dto.setTechnology(openings.getTechnology());
        dto.setSkill(openings.getSkill());
        dto.setExperience(openings.getExperience());
        dto.setLocation(openings.getLocation());
        dto.setEmploymentType(openings.getEmploymentType());
        dto.setStartDate(openings.getStartDate());
        dto.setEndDate(openings.getEndDate());
        dto.setStatus(openings.getStatus());
        dto.setCreatedAt(openings.getCreatedAt());
        dto.setUpdatedAt(openings.getUpdatedAt());
        dto.setDescription(openings.getDescription());
        dto.setPublicUrlKey(openings.getPublicUrlKey());

        if (openings.getPublicUrlKey() != null) {
            dto.setPublicUrl("http://localhost:3000/jobs/apply/" + openings.getPublicUrlKey());
        }

        // ================================
        // Resolve createdBy
        // ================================
        if (openings.getCreatedBy() != null) {
            dto.setCreatedBy(openings.getCreatedBy().getId());
            dto.setCreatedByName(
                    openings.getCreatedBy().getFirstName() + " " +
                            openings.getCreatedBy().getLastName()
            );
        } else {
            dto.setCreatedBy(null);
            dto.setCreatedByName("N/A");
        }

        if (openings.getUpdatedBy() != null) {
            dto.setUpdatedBy(openings.getUpdatedBy().getId());
        } else {
            dto.setUpdatedBy(null);
        }

        Long count = candidateRepository.countByOpeningId(openings.getId());
        dto.setCandidateCount(count != null ? count : 0L);

        return dto;
    }

    private String generatePublicKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0,10);
    }

    private void newOpeningEmail(Openings openings) {

        try {

            Integer creatorId = openings.getCreatedBy().getId();

            // 🎯 Fetch HR + Manager + Employee in ONE query
            List<Resource> users = resourceRepository
                    .findAllByPermissionIdInAndStatus(List.of(1, 2, 3), "ACTIVE");

            // 🎯 Filter emails (exclude creator)
            List<String> emails = users.stream()
                    .filter(r -> r.getId() != null && !r.getId().equals(creatorId))
                    .map(Resource::getEmail)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            if (emails.isEmpty()) {
                log.warn("No recipients found for opening email");
                return;
            }

            // 🎯 Send email
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(emails.toArray(new String[0]));
            // OR better:
            // message.setBcc(emails.toArray(new String[0]));

            message.setSubject("New Job Opening: " + openings.getName());

            message.setText(
                    "A new job opening has been created.\n\n" +
                            "Role: " + openings.getName() + "\n" +
                            "Technology: " + openings.getTechnology() + "\n" +
                            "Skills: " + openings.getSkill() + "\n" +
                            "Experience: " + openings.getExperience()
            );

            javaMailSender.send(message);
            log.info("Email sent successfully");

        } catch (Exception e) {
            log.error("Error sending opening email", e);
            // Don't throw — opening is already saved, just log the email failure
        }
    }

}
