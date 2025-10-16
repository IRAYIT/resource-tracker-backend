package com.ikonicit.resource.tracker.service;

import com.google.gson.Gson;
import com.ikonicit.resource.tracker.dto.OpeningsDTO;
import com.ikonicit.resource.tracker.entity.Openings;
import com.ikonicit.resource.tracker.exception.BadRequestException;
import com.ikonicit.resource.tracker.exception.MailSendFailedException;
import com.ikonicit.resource.tracker.exception.ResourceNotFoundException;
import com.ikonicit.resource.tracker.predicates.Predicates;
import com.ikonicit.resource.tracker.repository.OpeningsRepository;
import com.ikonicit.resource.tracker.repository.ResourceRepository;
import com.ikonicit.resource.tracker.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    Predicate<Object> isNotNull = Predicates.isNotNull;

    @Value("${spring.openingsEmailEnabled}")
    private Boolean openingsEmailEnabled;

    public OpeningsServiceImpl(OpeningsRepository openingsRepository) {
        this.openingsRepository = openingsRepository;
        log.info("object created for repository {}", openingsRepository);
    }

    @Override
    public OpeningsDTO create(OpeningsDTO openingsDTO) {
        Openings openings = openingsRepository.save(buildOpenings(openingsDTO));
        if(openingsEmailEnabled) {
//          newOpeningEmail(openingsDTO);
        }
        return buildOpeningsDTO(openings);
    }

    @Override
    public OpeningsDTO getOpening(Integer id) {
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
    public OpeningsDTO update(OpeningsDTO openingsDTO) {
        if (isNotNull.test(openingsDTO.getId()) && openingsDTO.getId() != 0) {
            getOpening(openingsDTO.getId());
        } else {
            throw new BadRequestException("Id is mandatory for update Opening");
        }
        return buildOpeningsDTO(openingsRepository.save(buildOpenings(openingsDTO)));
    }

    @Override
    public List<OpeningsDTO> getOpenings() {
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
    public List<OpeningsDTO> createOpenings(List<OpeningsDTO> openingsDTO) {

        List<Openings> openings = new ArrayList<>();
        openingsDTO.forEach(openingsDTO1 -> {
            openings.add(buildOpenings(openingsDTO1));
        });
        return buildOpeningsDTOList(openingsRepository.saveAll(openings));
    }

    private void newOpeningEmail(OpeningsDTO openingsDTO) {

        List<String> emails = resourceRepository.findEmails(Constants.TERMINATED);
        if (!emails.isEmpty() && isNotNull.test(emails)) {

            emails.forEach(email -> {
                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email);
                    message.setBcc("parasuramyerramsetty@gmail.com");
                    message.setSubject(openingsDTO.getName());
                    message.setText("Dear Applicant's, \n\n" + openingsDTO.getName()+"(sscreativelabs.com)" + " \n" + "Technology:" + openingsDTO.getTechnology() + " \n" + "Skills:" + openingsDTO.getSkill() +" \n"
                            + "Package:" + openingsDTO.getPayment() +" \n"+ "Salary Type:" + openingsDTO.getPaymentType()+" \n" + "Hours:" + openingsDTO.getHours() + "\n"+"ShiftTimings:" + openingsDTO.getShiftTimings()+"\n"
                            + "Experience:" + openingsDTO.getExperience()+"\n"+"If Interested please reach us on WHATSAPP ONLY +91 8331888832 and apply in our portal:http://resourcetracker.sscreativelabs.com"+"\n\n"
                            + "Thanks & Regards, "+"\n"+"Sunshine Creative Labs HR.");
                    javaMailSender.send(message);
                } catch (MailSendFailedException exception) {
                    log.error("Exception occurred during sending an email to the applicants for Opening Updates\t{}", openingsDTO.getName());
                    throw new MailSendFailedException("Exception occurred during sending an email to the applicant might be Email Wrong or Mail Server Down");
                }
            });
        }
    }

    private List<OpeningsDTO> buildOpeningsDTOList(List<Openings> openings) {

        List<OpeningsDTO> openingsDTOS = new ArrayList<>();
        openings.forEach(opening -> {
            openingsDTOS.add(buildOpeningsDTO(opening));
        });
        return openingsDTOS;
    }

    private Openings buildOpenings(OpeningsDTO openingsDTO) {
        openingsDTO.setStatus("ACTIVE");
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(openingsDTO), Openings.class);
    }

    private OpeningsDTO buildOpeningsDTO(Openings openings) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(openings), OpeningsDTO.class);
    }
}
