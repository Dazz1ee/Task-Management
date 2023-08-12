package com.taskmanagement.controllers;

import com.taskmanagement.models.ChangeReviewRequest;
import com.taskmanagement.models.Review;
import com.taskmanagement.models.ReviewRequest;
import com.taskmanagement.models.User;
import com.taskmanagement.services.ReviewService;
import com.taskmanagement.services.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    private final TaskService taskService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/leave")
    public ResponseEntity<?> leaveReview(@RequestBody ReviewRequest reviewRequest, @AuthenticationPrincipal User user){
        if (!taskService.isExists(reviewRequest.taskId())) {
            log.error("task [id {}] not exists. Review not added", reviewRequest.taskId());
            return ResponseEntity.badRequest().body(String.format("Task %d not exists", reviewRequest.taskId()));
        }

        if (!taskService.isUsersMonitoringTask(reviewRequest.taskId(), user.getUserId())) {
            log.warn("task [id {}] to be monitored", reviewRequest.taskId());
            return ResponseEntity.badRequest().body("You can't leave a review. Task to be monitored");
        }

        if (taskService.isCompleted(reviewRequest.taskId())) {
            return ResponseEntity.ok("The task already completed");
        }

        Review review = Review.builder()
                .taskId(reviewRequest.taskId())
                .message(reviewRequest.message())
                .ownerId(user.getUserId())
                .accepted(reviewRequest.isAccepted())
                .build();


        kafkaTemplate.send("review", review);
        return ResponseEntity.ok("review added");
    }

    @PostMapping("/change")
    public ResponseEntity<?> changeReview(@RequestBody ChangeReviewRequest request, @AuthenticationPrincipal User user){
        if (taskService.isCompleted(request.getReviewId())) {
            return ResponseEntity.ok("The task already completed");
        }
        if (reviewService.change(request, user)) {
            return ResponseEntity.ok("review updated");
        } else {
            return ResponseEntity.badRequest().body("User can't update this review");
        }
    }

    @GetMapping("/review-for-task")
    public ResponseEntity<?> getTaskReviews(@RequestParam Integer taskId, @AuthenticationPrincipal User user) {
        List<Review> reviews = reviewService.getReviewsForTaskId(taskId);

        if (reviews.isEmpty()) {
            return ResponseEntity.ok("no tasks");
        } else {
            return ResponseEntity.ok(reviews);
        }
    }
}
