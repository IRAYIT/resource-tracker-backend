package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentsDTO {
    private Integer attachmentId;
    private String fileName;

    public AttachmentsDTO(Integer attachmentId, String fileName) {
        this.attachmentId = attachmentId;
        this.fileName = fileName;
    }
}
