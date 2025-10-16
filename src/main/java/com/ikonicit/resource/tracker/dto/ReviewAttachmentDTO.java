package com.ikonicit.resource.tracker.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewAttachmentDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer attachmentId;
	private String fileName;
	
}
