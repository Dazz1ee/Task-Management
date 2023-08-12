package com.taskmanagement.mappers;

import com.taskmanagement.models.TaskStage;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskStageMapper implements RowMapper<TaskStage> {
    @Override
    public TaskStage mapRow(ResultSet rs, int rowNum) throws SQLException {
        return TaskStage.findByName(rs.getString("stage"));
    }
}
