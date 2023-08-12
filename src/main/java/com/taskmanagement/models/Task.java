package com.taskmanagement.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.beans.Transient;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    private Integer taskId;

    private String name;

    private String description;

    private TaskStage stage;

    private int priority;

    private LocalDateTime date;

    @JsonIgnore
    private User user;

    private int countObservers;

    private int accepted;

}
