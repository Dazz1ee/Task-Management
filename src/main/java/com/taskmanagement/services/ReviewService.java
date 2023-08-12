package com.taskmanagement.services;

import com.taskmanagement.dao.ReviewDao;
import com.taskmanagement.dao.TaskDao;
import com.taskmanagement.exceptions.ReviewNotFoundException;
import com.taskmanagement.exceptions.TaskNotFoundException;
import com.taskmanagement.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@KafkaListener(topics = "review")
public class ReviewService {
    private final ReviewDao reviewDao;
    private final TaskDao taskDao;

    @KafkaHandler
    public void leaveReview(Review review) {
        int balanceCountAccepted = review.getAccepted() ? 1 : 0;
        Optional<Review> optionalReview = reviewDao.findByTaskIdAndUserId(review.getTaskId(), review.getOwnerId());
        if (optionalReview.isPresent()) {
            Review prevReview = optionalReview.get();
            if (review.getAccepted() && prevReview.getAccepted() || !review.getAccepted() && !prevReview.getAccepted()) {
                balanceCountAccepted = 0;
            } else if (review.getAccepted()) {
                balanceCountAccepted = 1;
            } else {
                balanceCountAccepted = -1;
            }

            reviewDao.update(review);
        } else {
            reviewDao.add(review);
        }

        Task task = taskDao.findById(review.getTaskId()).orElseThrow(() -> new TaskNotFoundException("Task [id {}] not found while adding the review"));

        if (balanceCountAccepted == 1){

            if (task.getCountObservers() - 1 == task.getAccepted()) {
                task.setStage(TaskStage.COMPLETED);
            }

            task.setAccepted(task.getAccepted() + 1);
        } else if (balanceCountAccepted == -1) {

            if (task.getCountObservers() == task.getAccepted()) {
                task.setStage(TaskStage.WAIT_REVIEW);
            }

            task.setAccepted(task.getAccepted() - 1);
        } else {
            return;
        }

        taskDao.update(task);
    }

    @KafkaHandler
    public boolean change(ChangeReviewRequest request, User user) {
        Optional<Review> reviewOptional = reviewDao.findById(request.getReviewId());
        if (reviewOptional.isEmpty()) {
            throw new ReviewNotFoundException("Changed review not exists");
        }
        Review review = reviewOptional.get();
        if (review.getOwnerId().equals(user.getUserId())) {
            return false;
        } else {
            review.setMessage(review.getMessage());
            review.setAccepted(request.isAccepted());
            return  reviewDao.update(review) != 0;
        }
    }

    public List<Review> getReviewsForTaskId(Integer taskId) {
        return reviewDao.findByTaskId(taskId);
    }
}
