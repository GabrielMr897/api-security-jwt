package com.api.security.exceptions.conflict;

public class UserAlreadyExistsException extends RuntimeException  {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
