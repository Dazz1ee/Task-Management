package com.taskmanagement.dao;

import com.taskmanagement.models.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleDao {
    private final JdbcTemplate jdbcTemplate;

    public List<Role> getAllRole() {
        return jdbcTemplate.query("SELECT * FROM role", new BeanPropertyRowMapper<>(Role.class));
    }

    public Role getRoleByName(String name) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM role WHERE role = ?", new BeanPropertyRowMapper<>(Role.class), name);
        } catch (DataAccessException e) {
            log.error(String.format("role [%s] not exists"), name);
            return null;
        }
    }
}
