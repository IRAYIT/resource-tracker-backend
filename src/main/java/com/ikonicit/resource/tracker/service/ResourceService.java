package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.ResourceDTO;
import com.ikonicit.resource.tracker.dto.SendEmailDTO;
import com.ikonicit.resource.tracker.entity.ResourceAttachments;
import jakarta.mail.internet.AddressException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author Parasuram
 * @since 30-10-2020
 */
public interface ResourceService {


    ResourceDTO create(List<MultipartFile> attachments, String payload) throws ParseException, IOException, AddressException;

    ResourceDTO getResource(Integer id);

    ResourceDTO update(List<MultipartFile> attachments, String payload) throws ParseException;

    List<ResourceDTO> getAll();

    boolean deleteResource(Integer id);

    List<ResourceDTO> resourcesByManager(Integer managerId);

    List<ResourceDTO> getUnassignedResources();

    ResourceAttachments getAttachment(Integer attachmentId);

    List<ResourceDTO> getAllManagers();

    ResourceDTO findByResourceName(String resourceName);

    String checkResourceName(String resourceName);

    String sendEmail(SendEmailDTO sendEmailDTO);

    String checkEmail(String email);

}
