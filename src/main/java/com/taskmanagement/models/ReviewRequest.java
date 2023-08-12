package com.taskmanagement.models;

import org.springframework.lang.Nullable;

public record ReviewRequest (Integer taskId, String message, Boolean isAccepted) {

}
