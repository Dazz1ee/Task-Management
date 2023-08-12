package com.taskmanagement.mappers;

import com.taskmanagement.models.Review;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("review_id"))
                .taskId(rs.getInt("task_id"))
                .ownerId(rs.getInt("owner_id"))
                .message(rs.getString("message"))
                .accepted(rs.getBoolean("status"))
                .build();
    }
}
