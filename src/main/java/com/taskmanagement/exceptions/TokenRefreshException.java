package com.taskmanagement.exceptions;

public class TokenRefreshException extends RuntimeException{
    public TokenRefreshException(String token, String message) {
        super(String.format("Not valid [%s]: %s", token, message));
    }
}
