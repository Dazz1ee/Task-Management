package com.taskmanagement.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Nullable
    private Integer reviewId;

    private Integer taskId;

    private Integer ownerId;

    @Nullable
    private String message;

    private Boolean accepted;

}
