package com.taskmanagement.exceptions;

public class TaskNotFoundException extends RuntimeException{
    public TaskNotFoundException (Integer id) {
        super(String.format("task [id  %d] not exists", id));
    }

    public TaskNotFoundException (String name) {
        super(String.format("task [%s] not exists", name));
    }
}
