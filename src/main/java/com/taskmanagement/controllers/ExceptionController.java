package com.taskmanagement.controllers;

import com.taskmanagement.exceptions.*;
import com.taskmanagement.exceptions.RoleNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(value = TokenRefreshException.class)
    public ResponseEntity<?> needLogin(TokenRefreshException exception){
        log.debug("user need login");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @ExceptionHandler(value = TaskStageException.class)
    public ResponseEntity<?> stageException(TaskStageException exception){
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler(value = TaskNotFoundException.class)
    public ResponseEntity<?> needLogin(TaskNotFoundException exception){
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }


    @ExceptionHandler(value =RoleNotFoundException.class)
    public ResponseEntity<?> roleNotFound(RoleNotFoundException exception){
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }


    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<?> userNotFound(UserNotFoundException exception){
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }


    @ExceptionHandler(value = ReviewNotFoundException.class)
    public ResponseEntity<?> userNotFound(ReviewNotFoundException exception){
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<?> userNotFound(AccessDeniedException exception){
        log.debug(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Need authorization");
    }
}
