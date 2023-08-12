package com.taskmanagement.dao;

import com.taskmanagement.models.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {
    Optional<Review> findById(int reviewId);
    int add(Review review);
    int deleteByReviewId(int reviewId);
    int update(Review review);

    Optional<Review> findByTaskIdAndUserId(Integer taskId, Integer ownerId);

    List<Review> findByTaskId(Integer taskId);
}
