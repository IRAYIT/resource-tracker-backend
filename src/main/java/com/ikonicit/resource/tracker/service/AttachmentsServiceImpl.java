package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.AttachmentsDTO;
import com.ikonicit.resource.tracker.dto.ViewAttachmentDTO;
import com.ikonicit.resource.tracker.entity.Resource;
import com.ikonicit.resource.tracker.entity.ResourceAttachments;
import com.ikonicit.resource.tracker.exception.ResourceNotFoundException;
import com.ikonicit.resource.tracker.repository.ResourceAttachmentsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*@author Parasuram
 *@since 11-05-2021
 */


@Slf4j
@Service
public class AttachmentsServiceImpl implements AttachmentsService {


    @Autowired
    private ResourceAttachmentsRepository resourceAttachmentsRepository;

    @Autowired
    private ObjectFactory<ResourceAttachments> attachmentsObjectFactory;

    @Override
    public ViewAttachmentDTO getAttachment(Integer id) {
        return resourceAttachmentsRepository.getAttachment(id);
    }

    @Override
    public List<AttachmentsDTO> getAttachmentsByResourceId(Integer id) {
        return resourceAttachmentsRepository.getAttachmentsByResourceId(id);
    }

    @Override
    public String createAttachment(List<MultipartFile> attachments, Integer id) {
        resourceAttachmentsRepository.saveAll(buildAttachments(attachments, id));
        return "Attachment created successfully";
    }

    private List<ResourceAttachments> buildAttachments(List<MultipartFile> attachments, Integer id) {
        List<ResourceAttachments> resourceAttachments = new ArrayList<>();
        if (!CollectionUtils.isEmpty(attachments)) {
            attachments.stream().forEach(resourceAttachment -> {
                ResourceAttachments attachment = attachmentsObjectFactory.getObject();
                attachment.setFileName(resourceAttachment.getOriginalFilename());
                try {
                    attachment.setAttachment(resourceAttachment.getBytes());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                Resource resource = new Resource();
                resource.setId(id);
                attachment.setResource(resource);
                attachment.setContentType(resourceAttachment.getContentType());
                attachment.setCreatedAt(new Date());
                resourceAttachments.add(attachment);
            });
        }
        return resourceAttachments;
    }

    @Override
    public String deleteAttachment(Integer id) {
        if (resourceAttachmentsRepository.findById(id).isPresent()) {
            resourceAttachmentsRepository.deleteById(id);
            return "Attachment Deleted Successfully";
        }
        throw new ResourceNotFoundException("Invalid Attachment Id" + id);
    }
}
