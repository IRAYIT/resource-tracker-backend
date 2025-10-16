package com.ikonicit.resource.tracker.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialsDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	    private Integer id;
	    private String email;
	    private String password;
	    private Date createdAt;
	    private String createdBy;
	    private Date updatedAt;
	    private String updatedBy;
	
	    
	    
}
