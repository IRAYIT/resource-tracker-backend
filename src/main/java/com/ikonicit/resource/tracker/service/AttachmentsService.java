package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.AttachmentsDTO;
import com.ikonicit.resource.tracker.dto.ViewAttachmentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentsService {
    public ViewAttachmentDTO getAttachment(Integer id);

    String deleteAttachment(Integer id);

    List<AttachmentsDTO> getAttachmentsByResourceId(Integer id);

    String createAttachment(List<MultipartFile> attachments, Integer id);
}
