package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Setter
@Component
@Scope(scopeName = "prototype")
public class ResourceAttachmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer attachmentId;
    private String fileName;

}
