package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewAttachmentDTO {

    private String contentType;
    private byte[] attachment;

    public ViewAttachmentDTO(String contentType, byte[] attachment) {
        this.contentType = contentType;
        this.attachment = attachment;
    }
}
