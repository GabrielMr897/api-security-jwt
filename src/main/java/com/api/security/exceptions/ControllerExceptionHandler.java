package com.api.security.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String UNPROCESSABLE_ENTITY_MESSAGE = "Unprocessable Entity: The request was well-formed but unable to be followed due to semantic errors."; // 422
	private static final String CONFLICT_MESSAGE = "Conflict: The request could not be completed due to a conflict with the current state of the target resource."; // 409

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class,
			SizeLimitExceededException.class,
			IllegalArgumentException.class})
	public ResponseEntity<ErrorResponse> handleExceptions(
			RuntimeException ex) {
		log.error("Error: ", ex);
		return new ResponseEntity<>(
				new ErrorResponse(
						HttpStatus.UNPROCESSABLE_ENTITY,
						UNPROCESSABLE_ENTITY_MESSAGE,
						ex.getLocalizedMessage()),
				HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler({ AuthException.class })
	public ResponseEntity<ErrorResponse> handleAuthExceptions(
			RuntimeException ex) {
		log.error("Error: ", ex);
		return new ResponseEntity<>(
				new ErrorResponse(HttpStatus.UNAUTHORIZED,
						CONFLICT_MESSAGE,
						ex.getLocalizedMessage()),
				HttpStatus.UNAUTHORIZED);
	}
    
	@SuppressWarnings("null")
    @Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
			org.springframework.http.HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request) {
		List<String> errors = ex.getBindingResult().getFieldErrors()
				.stream()
				.map(fieldError -> fieldError.getField() + ": "
						+ fieldError.getDefaultMessage())
				.collect(Collectors.toList());
		ErrorResponse errorResponse = new ErrorResponse(status,
				"Validation Error",
				errors);
		return new ResponseEntity<>(errorResponse, headers, status);
	}

	@SuppressWarnings("null")
    @Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers,
			HttpStatusCode status, WebRequest request) {
		Throwable rootCause = ex.getRootCause();
		String errorMessage = "Malformed JSON request";
		List<String> errors = new ArrayList<>();

		// Check if the root cause is due to invalid enum value
		if (rootCause instanceof InvalidFormatException) {
			InvalidFormatException invalidFormatEx = (InvalidFormatException) rootCause;
			Object value = invalidFormatEx.getValue();
			String expectedType = invalidFormatEx.getTargetType()
					.getSimpleName();
			String message = String.format(
					"Invalid value '%s' for type '%s'",
					value, expectedType);
			errorMessage = "Invalid JSON value: " + message;
			errors.add(message);
		} else {
			errors.add(errorMessage);
		}

		ErrorResponse errorResponse = new ErrorResponse(status,
				errorMessage, errors);
		return new ResponseEntity<>(errorResponse, headers, status);
	}
}