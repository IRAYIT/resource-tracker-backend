package com.ikonicit.resource.tracker.exception;

/**
 * It will be returned if request is bad from the UI
 * @author Parasuram
 *
 */
public class BadRequestException extends RuntimeException  {


	private static final long serialVersionUID = 1L;

	public BadRequestException(String exception) {
	        super(exception);
	    }
}
