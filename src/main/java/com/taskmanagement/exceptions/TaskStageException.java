package com.taskmanagement.exceptions;

public class TaskStageException extends RuntimeException {
    public TaskStageException(String stage) {
        super(String.format("[%s] not found", stage));
    }
}
