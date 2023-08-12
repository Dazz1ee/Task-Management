package com.taskmanagement.dao;

import com.taskmanagement.mappers.UserMapper;
import com.taskmanagement.models.Role;
import com.taskmanagement.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class UserDaoImp implements UserDao{

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertIntoUsers;

    @Autowired
    public UserDaoImp(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        insertIntoUsers = new SimpleJdbcInsert(jdbcTemplate).withTableName("users").usingGeneratedKeyColumns("user_id");
    }

    @Override
    public Optional<User> findUserById(int id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT users.user_id AS user_id, users.name AS name, users.email AS email, users.password AS password, role.role_id AS role_id, role.role AS role "  +
                            "FROM users JOIN role_user ON users.user_id = role_user.user_id " +
                            "JOIN role ON role_user.role_id = role.role_id WHERE users.user_id = ?",
                    new UserMapper(), id));

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        try {
            return jdbcTemplate.query("SELECT users.user_id AS user_id, users.name AS name, users.email AS email, users.password AS password, role.role_id AS role_id, role.role AS role " +
                    "FROM users JOIN role_user ON users.user_id = role_user.user_id " +
                    "JOIN role ON role_user.role_id = role.role_id WHERE users.email = ?",
                    new UserMapper(), email).stream().findFirst();
        } catch (DataAccessException e) {
            log.error("FAILED TO FIND USER. {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public int saveUser(User user) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", user.getName());
        parameters.put("email", user.getEmail());
        parameters.put("password", user.getPassword());

        try {
            int id =  insertIntoUsers.executeAndReturnKey(parameters).intValue();

            user.setUserId(id);
            jdbcTemplate.batchUpdate("INSERT INTO role_user(role_id, user_id) VALUES (? , ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, user.getRoles().get(i).getRoleId());
                            ps.setInt(2, user.getUserId());
                        }

                        @Override
                        public int getBatchSize() {
                            return user.getRoles().size();
                        }
                    });
            return id;
        } catch (DuplicateKeyException e) {
            return 0;
        }
    }

    @Override
    public int deleteUserById(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);
    }

    @Override
    public int deleteUserByEmail(String email) {
        return jdbcTemplate.update("DELETE FROM users WHERE email = ?", email);
    }

    @Override
    public boolean addRole(User user, Role role) {
        try {
            jdbcTemplate.update("INSERT INTO role_user (role_id, user_id) VALUES (? , ?)", role.getRoleId(), user.getUserId());
            user.getRoles().add(role);
            return true;
        } catch (DataAccessException exception) {
            log.error("Role {} was failed to added", role.getRole().toString());
            return false;
        }
    }
}
