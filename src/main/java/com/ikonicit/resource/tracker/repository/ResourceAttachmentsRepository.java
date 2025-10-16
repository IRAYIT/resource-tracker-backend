package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.dto.AttachmentsDTO;
import com.ikonicit.resource.tracker.dto.ViewAttachmentDTO;
import com.ikonicit.resource.tracker.entity.ResourceAttachments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceAttachmentsRepository extends JpaRepository<ResourceAttachments, Integer> {
    @Query(value="Select new com.ikonicit.resource.tracker.dto.ViewAttachmentDTO(a.contentType,a.attachment) from ResourceAttachments a where a.attachmentId = ?1")
    public ViewAttachmentDTO getAttachment(Integer id);

    @Query(value="Select new com.ikonicit.resource.tracker.dto.AttachmentsDTO(a.attachmentId,a.fileName) from ResourceAttachments a where a.resource.id = ?1")
    List<AttachmentsDTO> getAttachmentsByResourceId(Integer id);
}
