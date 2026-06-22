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
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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


    @Value("${spring.mail.username}")
    private String mailUsername;

    public OpeningsServiceImpl(OpeningsRepository openingsRepository) {
        this.openingsRepository = openingsRepository;
        log.info("object created for repository {}", openingsRepository);
    }

    @Override
    public OpeningsResponseDTO create(OpeningsDTO openingsDTO) {

        Openings openings = buildOpenings(openingsDTO);

        openings.setStatus("ACTIVE");


        Resource createdBy = resourceRepository.findById(openingsDTO.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("HR not found"));

        Resource updatedBy = resourceRepository.findById(openingsDTO.getUpdatedBy())
                .orElseThrow(() -> new RuntimeException("HR not found"));

        openings.setCreatedBy(createdBy);
        openings.setUpdatedBy(updatedBy);

        openings.setPublicUrlKey(generatePublicKey());

        openings = openingsRepository.save(openings);

        if (openingsEmailEnabled) {
//           newOpeningEmail(openings);
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

public List<OpeningsResponseDTO> getAllOpenings() {
    return buildOpeningsDTOList(openingsRepository.findAllByOrderByIdDesc());
}

private List<OpeningsResponseDTO> buildOpeningsDTOList(List<Openings> openings) {
    List<OpeningsResponseDTO> openingsDTOS = new ArrayList<>();
    openings.forEach(opening -> {
        openingsDTOS.add(buildOpeningsDTO(opening));
    });
    return openingsDTOS;
}

    private Openings buildOpenings(OpeningsDTO dto) {

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
//        dto.setHours(openings.getHours());
//        dto.setShiftTimings(openings.getShiftTimings());
//        dto.setPayment(openings.getPayment());
//        dto.setPaymentType(openings.getPaymentType());
        dto.setTechnology(openings.getTechnology());
        dto.setSkill(openings.getSkill());
        dto.setExperience(openings.getExperience());
        dto.setLocation(openings.getLocation());
//        dto.setEmploymentType(openings.getEmploymentType());
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

            List<Resource> users = resourceRepository
                    .findAllByPermissionIdInAndStatus(List.of(1, 2, 3, 4), "ACTIVE");

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

            // ✅ Send in batches of 50
            int batchSize = 50;
            int totalBatches = (int) Math.ceil((double) emails.size() / batchSize);

            for (int i = 0; i < emails.size(); i += batchSize) {
                List<String> batch = emails.subList(i, Math.min(i + batchSize, emails.size()));
                sendBatch(openings, batch);

                log.info("Sent batch {}/{}", (i / batchSize) + 1, totalBatches);

                // ✅ Fix
                if (i + batchSize < emails.size()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.warn("Batch email interrupted");
                        break;
                    }
                }
            }

            log.info("Opening email sent successfully to {} recipients", emails.size());

        } catch (Exception e) {
            log.error("Error sending opening email", e);
        }
    }

    private void sendBatch(Openings openings, List<String> batch) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String companyName = getCompanyName(openings.getLocation());
        String applyLink = "http://localhost:3000/jobs/apply/" + openings.getPublicUrlKey();

        helper.setFrom(mailUsername);
        helper.setTo(mailUsername);
        helper.setBcc(batch.toArray(new String[0]));
        helper.setSubject("New Job Opening at " + companyName + ": " + openings.getName());

        String htmlContent = "<html>" +
                "<body style='margin:0; padding:0; font-family: Arial, sans-serif; background-color: #f4f6f9;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f6f9; padding: 30px 0;'>" +
                "<tr><td align='center'>" +
                "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; border-radius:10px; overflow:hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1);'>" +

                // Header
                "<tr>" +
                "<td style='background: linear-gradient(135deg, #1e3a8a, #3b82f6); padding: 30px 40px; text-align:center;'>" +
                "<h1 style='color:#ffffff; margin:0; font-size:24px; letter-spacing:1px;'>New Job Opening</h1>" +
                "<p style='color:#bfdbfe; margin:8px 0 0 0; font-size:14px;'>" + companyName + " — An exciting opportunity has just been posted!</p>" +
                "</td>" +
                "</tr>" +

                // Body
                "<tr>" +
                "<td style='padding: 30px 40px;'>" +
                "<p style='font-size:15px; color:#374151; margin-bottom:20px;'>" +
                "Hi Team,<br><br>" +
                "We're excited to announce that a <strong>new job opening</strong> has been created at " +
                "<strong>" + companyName + "</strong>. Please find the details below:" +
                "</p>" +

                // Details Card
                "<table width='100%' cellpadding='0' cellspacing='0' " +
                "style='background-color:#f0f4ff; border-left: 4px solid #3b82f6; border-radius:6px; padding:0; margin-bottom:24px;'>" +
                "<tr><td style='padding: 20px 24px;'>" +
                "<table width='100%' cellpadding='6' cellspacing='0'>" +

                "<tr>" +
                "<td style='font-size:13px; color:#6b7280; width:140px;'>Role</td>" +
                "<td style='font-size:14px; color:#111827; font-weight:bold;'>" + openings.getName() + "</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='font-size:13px; color:#6b7280;'>Technology</td>" +
                "<td style='font-size:14px; color:#111827;'>" + openings.getTechnology() + "</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='font-size:13px; color:#6b7280;'>Skills Required</td>" +
                "<td style='font-size:14px; color:#111827;'>" + openings.getSkill() + "</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='font-size:13px; color:#6b7280;'>Experience</td>" +
                "<td style='font-size:14px; color:#111827;'>" + openings.getExperience() + " years</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='font-size:13px; color:#6b7280;'>Location</td>" +
                "<td style='font-size:14px; color:#111827;'>" + openings.getLocation() + "</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='font-size:13px; color:#6b7280;'>Company</td>" +
                "<td style='font-size:14px; color:#111827; font-weight:bold;'>" + companyName + "</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='font-size:13px; color:#6b7280;'>Apply Link</td>" +
                "<td style='font-size:14px;'><a href='" + applyLink + "' style='color:#3b82f6;'>" + applyLink + "</a></td>" +
                "</tr>" +

                "<tr>" +
                "<td style='font-size:13px; color:#6b7280;'>Posted On</td>" +
                "<td style='font-size:14px; color:#111827;'>" + java.time.LocalDate.now() + "</td>" +
                "</tr>" +

                "</table>" +
                "</td></tr>" +
                "</table>" +

                // Referral message
                "<p style='font-size:14px; color:#374151; margin-bottom:16px;'>" +
                "If you know someone who would be a great fit, share the apply link with them." +
                "</p>" +

                "<p style='font-size:14px; color:#374151; margin-bottom:24px;'>" +
                "Let's grow our team with the best talent!" +
                "</p>" +

                "</td></tr>" +

                // Footer
                "<tr>" +
                "<td style='background-color:#f9fafb; padding:20px 40px; text-align:center; border-top:1px solid #e5e7eb;'>" +
                "<p style='font-size:12px; color:#9ca3af; margin:0;'>" +
                "This is an automated notification from <strong>" + companyName + "</strong>.<br>" +
                "Please do not reply to this email." +
                "</p>" +
                "</td></tr>" +

                "</table>" +
                "</td></tr>" +
                "</table>" +
                "</body></html>";

        helper.setText(htmlContent, true);
        javaMailSender.send(message);
    }

    private String getCompanyName(String location) {
        if (location == null) return "I-Ray IT Solutions";
        return switch (location.trim().toLowerCase()) {
            case "sweden" -> "I-Ray IT Solutions AB";
            case "usa"    -> "I-Ray IT Solutions INC";
            default       -> "I-Ray IT Solutions";
        };
    }
}
