package com.taskmanagement.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username, String cond) {
        super(String.format("User %s not found exception, %s", username, cond));
    }
}
