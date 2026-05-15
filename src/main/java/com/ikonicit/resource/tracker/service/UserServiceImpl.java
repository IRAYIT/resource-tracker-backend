package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.*;
import com.ikonicit.resource.tracker.entity.Credentials;
import com.ikonicit.resource.tracker.entity.Resource;
import com.ikonicit.resource.tracker.exception.ResourceNotFoundException;
import com.ikonicit.resource.tracker.predicates.Predicates;
import com.ikonicit.resource.tracker.repository.CredentialsRepository;
import com.ikonicit.resource.tracker.repository.ResourceAttachmentsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    JavaMailSender JavaMailSender;

    @Autowired
    private ObjectFactory<ResourceDTO> resourceDTOObjectFactory;

    @Autowired
    private ObjectFactory<PermissionDTO> permissionDTOObjectFactory;

    @Autowired
    ResourceAttachmentsRepository resourceAttachmentsRepository;

    @Autowired
    CredentialsRepository credentialsRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JavaMailSender javaMailSender;


    @Override
    public String changePassword(ChangePasswordDTO changePasswordDTO) {
        Credentials credentials = credentialsRepository.findById(changePasswordDTO.getId()).get();
        if (Predicates.isNotNull.test(credentials)) {
            if (credentials.getPassword().equals(changePasswordDTO.getOldPassword())) {
                credentials.setPassword(changePasswordDTO.getNewPassword());
                credentialsRepository.saveAndFlush(credentials);
                log.info("Password Changed Successfully");
                return "Password Changed Successfully";
            } else {
                return "Old Password Doesn't Match";
            }
        }
        throw new ResourceNotFoundException("Given credentials not found");
    }

    @Override
    public ResourceDTO login(LoginDTO loginDTO) {
        if (Predicates.isNotNull.test(credentialsRepository.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword()))) {
            Credentials credentials = credentialsRepository.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
            Resource employee = credentials.getResource();
            ResourceDTO employeeDTO = resourceDTOObjectFactory.getObject();
            BeanUtils.copyProperties(employee, employeeDTO);
            PermissionDTO permission = permissionDTOObjectFactory.getObject();
            BeanUtils.copyProperties(employee.getPermission(), permission);
            employeeDTO.setPermission(permission);
            return employeeDTO;
        }
        throw new ResourceNotFoundException("User not found");
    }

    @Override
    public String forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        Credentials credentials = credentialsRepository.findByEmail(forgotPasswordDTO.getEmail());
        if (Predicates.isNotNull.test(credentials)) {
            credentials.setPassword(forgotPasswordDTO.getPassword());
            credentialsRepository.save(credentials);
            return "Password Changed Successfully";
        }
        throw new ResourceNotFoundException("Invalid Email. Please Enter Correct Email");
    }

    @Override
    public Boolean checkEmail(String email) {
        Credentials credentials = credentialsRepository.findByEmail(email);
        return credentials != null;
    }

    public String sendOtp(String email) {
        Credentials credentials = credentialsRepository.findByEmail(email);
        if (credentials == null) {
            throw new ResourceNotFoundException("No account found with this email.");
        }
        String otp = otpService.generateOtp(email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP For Password Reset - Candidate Tracker");
        message.setText("Your OTP for password reset is: " + otp + "\nThis OTP is valid for one time use only.");
        javaMailSender.send(message);
        return "OTP sent to your email";
    }

    public String verifyOtp(String email, String otp) {
        if (otpService.validateOtp(email, otp)) {
            return "OTP Verified";
        }
        return "Invalid OTP";
    }


}
