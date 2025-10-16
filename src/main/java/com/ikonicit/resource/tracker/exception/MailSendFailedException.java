package com.ikonicit.resource.tracker.exception;

/**
 * It will be returned if Email Service is down or Invalid Email found
 * @author Parasuram
 *
 */
public class MailSendFailedException extends RuntimeException  {


	private static final long serialVersionUID = 1L;

	public MailSendFailedException(String exception) {
	        super(exception);
	    }
}
