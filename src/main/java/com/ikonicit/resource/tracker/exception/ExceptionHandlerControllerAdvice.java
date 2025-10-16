package com.ikonicit.resource.tracker.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized Exception to handle all exceptions
 * @author Parasuram
 *
 */
@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler   {

	
	@ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ExceptionResponse error = new ExceptionResponse("Server Error", details);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

	@ExceptionHandler(MailSendFailedException.class)
	public final ResponseEntity<ExceptionResponse> mailSendFailedException(MailSendFailedException ex, WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ExceptionResponse error = new ExceptionResponse("Mail Send Failed Error Or Wrong Receipt Mail Address Error", details);
		return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public final ResponseEntity<ExceptionResponse>  handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
		  List<String> details = new ArrayList<>();
		    details.add(ex.getLocalizedMessage());
		ExceptionResponse error = new ExceptionResponse("Record Not Found", details);
		  return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<ExceptionResponse>  handleBadRequest(BadRequestException ex, WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ExceptionResponse error = new ExceptionResponse("BadRequest Found", details);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	
	    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		 Map<String,Object> body = new LinkedHashMap<>();
		 body.put("timestamp", LocalDate.now());
		 body.put("status",status.value());
	        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x->x.getDefaultMessage()).collect(Collectors.toList());
	     body.put("errors",errors);
	        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	    }
}
