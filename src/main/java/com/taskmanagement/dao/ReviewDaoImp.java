package com.taskmanagement.dao;

import com.taskmanagement.mappers.ReviewMapper;
import com.taskmanagement.models.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class ReviewDaoImp implements ReviewDao{
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ReviewDaoImp(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("review").usingGeneratedKeyColumns("review_id");
    }

    @Override
    public Optional<Review> findById(int reviewId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM review WHERE review_id = ?", new ReviewMapper(), reviewId));
        } catch (DataAccessException ex) {
            log.warn("Can't find review with id {}", reviewId);
            return Optional.empty();
        }
    }

    @Override
    public int add(Review review) {
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("task_id", review.getTaskId());
            args.put("owner_id", review.getOwnerId());
            args.put("message", review.getMessage());
            args.put("status", review.getAccepted());
            return jdbcInsert.executeAndReturnKey(args).intValue();
        } catch (DataAccessException ex) {
            log.error("The review not added");
            return 0;
        }
    }

    @Override
    public int deleteByReviewId(int reviewId) {
        return jdbcTemplate.update("DELETE FROM review WHERE review_id = ?", reviewId);
    }

    @Override
    public int update(Review review) {
        try {
            return jdbcTemplate.update("UPDATE review SET message = ?, status = ? WHERE review_id = ?", review.getMessage(), review.getAccepted(), review.getReviewId());
        } catch (DataAccessException ex) {
            log.error("The review not updated [{}]", ex.getMessage());
            return 0;
        }
    }


    @Override
    public Optional<Review> findByTaskIdAndUserId(Integer taskId, Integer ownerId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM review WHERE task_id = ? and owner_id = ?", new ReviewMapper(), taskId, ownerId));
        } catch (DataAccessException ex) {
            log.warn("Can't find review [taskId {}, ownerId{}]", taskId, ownerId);
            return Optional.empty();
        }
    }

    @Override
    public List<Review> findByTaskId(Integer taskId) {
        return jdbcTemplate.query("SELECT * FROM review WHERE task_id = ?", new ReviewMapper(), taskId);
    }
}
