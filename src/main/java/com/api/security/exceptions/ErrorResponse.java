package com.api.security.exceptions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Data;

@Data
public class ErrorResponse implements Serializable {
    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ErrorResponse(HttpStatus status, String message, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ErrorResponse(HttpStatus status, String message, String error) {
        super();
        this.status = status;
        this.message = message;
        errors = Arrays.asList(error);
    }

    public ErrorResponse(HttpStatusCode status2, String message2, List<String> errors2) {
        super();
        this.status = (HttpStatus) status2;
        this.message = message2;
        errors = errors2;
    }

    public ErrorResponse(HttpStatusCode status, String message, String error) {
        super();
        this.status = (HttpStatus) status;
        this.message = message;
        errors = Arrays.asList(error);
    }
}