package com.energyms.energyms.validation;

import java.io.IOException;
import java.nio.file.AccessDeniedException;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.client.HttpClientErrorException;


@ControllerAdvice
public class ValidationHandler  {


	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex)
	{
		return new ResponseEntity<>("method is not valid",HttpStatus.METHOD_NOT_ALLOWED);
		
	}
	
	

	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex)
	{
		return new ResponseEntity<>("method is not supported",HttpStatus.METHOD_NOT_ALLOWED);
		
	}
	

	
	@ExceptionHandler(HttpClientErrorException.Conflict.class)
	public ResponseEntity<?> handleConflictException(HttpClientErrorException ex)
	{
		return new ResponseEntity<>("occcured conflict",HttpStatus.CONFLICT);
		
	}
	

	

	@ExceptionHandler(HttpClientErrorException.NotFound.class)
	public ResponseEntity<?> handleConflictExceptionn(HttpClientErrorException ex)
	{
		return new ResponseEntity<>("required element is not found",HttpStatus.NOT_FOUND);
		
	}
	

	

	
	@ExceptionHandler(IOException.class)
	public ResponseEntity<?> handleIOException(IOException ex)
	{
		return new ResponseEntity<>("input output exception",HttpStatus.INTERNAL_SERVER_ERROR);
		
	}

	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex)
	{
		return new ResponseEntity<>("unreadable message",HttpStatus.BAD_REQUEST);
		
	}
	
	

	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex)
	{
		return new ResponseEntity<>("please check crendetials",HttpStatus.UNAUTHORIZED);
		
	}
}

