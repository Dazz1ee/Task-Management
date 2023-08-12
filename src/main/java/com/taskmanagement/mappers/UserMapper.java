package com.taskmanagement.mappers;

import com.taskmanagement.models.Role;
import com.taskmanagement.models.RoleEnum;
import com.taskmanagement.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class UserMapper implements RowMapper<User> {
    private final HashMap<Integer, User> container;

    public UserMapper() {
        container = new HashMap<>();
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = container.getOrDefault(rs.getInt("user_id"), User.builder()
                .userId(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .password(rs.getString("password"))
                .roles(new ArrayList<>())
                .build());

        user.getRoles().add(new Role(rs.getInt("role_id"),
                RoleEnum.findByName(rs.getString("role"))));

        container.put(user.getUserId(), user);
        return user;
    }
}
