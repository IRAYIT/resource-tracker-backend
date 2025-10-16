package com.ikonicit.resource.tracker.exception;

/**
 * It will be returned if no resource found in the backend
 * @author Parasuram
 *
 */
public class ResourceNotFoundException extends RuntimeException  {

	
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String exception) {
	        super(exception);
	    }
}
