package com.ikonicit.resource.tracker.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
/**
 * ExceptionResponse to return errorMessage and details
 * @author Parasuram
 *
 */
public class ExceptionResponse implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	
	public ExceptionResponse(String errorMessage,List<String> details) {
		super();
		this.errorMessage = errorMessage;
		this.details = details;
	}
	
	
	private String errorMessage;
	private List<String> details;
	
	
}
