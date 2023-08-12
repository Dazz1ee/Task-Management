package com.taskmanagement.mappers;

import com.taskmanagement.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TaskMapper implements RowMapper<Task> {
    Map<Integer, Task> container = new HashMap<>();
    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = container.getOrDefault(rs.getInt("task_id"), Task.builder()
                .taskId(rs.getInt("task_id"))
                .description(rs.getString("task_description"))
                .name(rs.getString("task_name"))
                .date(rs.getTimestamp("date").toLocalDateTime())
                .accepted(rs.getInt("accepted"))
                .countObservers(rs.getInt("count_observers"))
                .priority(rs.getInt("priority"))
                .stage(TaskStage.findByName(rs.getString("stage")))
                .build());

        if (task.getUser() == null && rs.getInt("user_id") != 0) {
            try {
                task.setUser(User.builder()
                        .userId(rs.getInt("user_id"))
                        .email(rs.getString("email"))
                        .password(rs.getString("user_password"))
                        .name(rs.getString("username"))
                        .roles(new ArrayList<>())
                        .build());
            } catch (SQLException e) {
                log.debug("for task '{}' not exist user", task.getUser());
            }
        }
        if (task.getUser() != null) {
            task.getUser().getRoles().add(new Role(
                    rs.getInt("role_id"),
                    RoleEnum.findByName(rs.getString("role")))
            );
        }
        return task;

    }
}
