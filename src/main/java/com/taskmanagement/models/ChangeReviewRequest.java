package com.taskmanagement.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
public class ChangeReviewRequest {

    private Integer reviewId;

    @Nullable
    private String message;

    private boolean isAccepted;

    public ChangeReviewRequest(Integer reviewId, @Nullable String message, boolean isAccepted) {
        this.reviewId = reviewId;
        this.message = message;
        this.isAccepted = isAccepted;
    }

    public ChangeReviewRequest(Integer reviewId, boolean isAccepted) {
        this.reviewId = reviewId;
        this.isAccepted = isAccepted;
    }
}
