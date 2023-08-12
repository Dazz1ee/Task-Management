package com.taskmanagement.dao;

import com.taskmanagement.mappers.TaskMapper;
import com.taskmanagement.mappers.TaskStageMapper;
import com.taskmanagement.models.TaskStage;
import com.taskmanagement.models.Task;
import com.taskmanagement.models.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
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
public class TaskDaoImp implements TaskDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public TaskDaoImp(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("task").usingGeneratedKeyColumns("task_id");
    }

    @Override
    public int add(Task task) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", task.getName());
            parameters.put("description", task.getDescription());
            parameters.put("date", task.getDate());
            parameters.put("priority", task.getPriority());
            parameters.put("accepted", task.getAccepted());
            parameters.put("count_observers", task.getCountObservers());
            parameters.put("stage", task.getStage().toString());

            if (task.getUser() != null) {
                parameters.put("user_id", task.getUser().getUserId());
            }

            task.setTaskId(jdbcInsert.executeAndReturnKey(parameters).intValue());
            log.info("Task with name '{}' saved", task.getName());

            return task.getTaskId();
        } catch (DataAccessException e) {
            log.error("Task with name '{}' not saved", task.getName());
            return 0;
        }
    }


    @Override
    public Optional<Task> findById(int id) {
        try {
                return Optional.ofNullable(
                  jdbcTemplate.queryForObject("SELECT users.name AS username, users.email AS email, " +
                          "users.password AS user_password, task.user_id AS user_id, " +
                          "role.role_id AS role_id, role.role AS role, " +
                          "task.name AS task_name, task.description AS task_description, " +
                          "task.priority AS priority, task.date AS date, " +
                          "task.task_id AS task_id, task.stage AS stage, " +
                          "task.accepted AS accepted, task.count_observers AS count_observers " +
                          "FROM task LEFT JOIN users ON task.user_id = users.user_id LEFT JOIN " +
                          "role_user ON task.user_id = role_user.user_id " +
                          "LEFT JOIN role ON role_user.role_id = role.role_id WHERE task.task_id = ?", new TaskMapper(), id)
                );
        } catch (DataAccessException a) {
            return Optional.empty();
        }
    }

    @Override
    public List<Task> getAll() {
        try {
            return jdbcTemplate.query("SELECT users.name AS username, users.email AS email, " +
                    "users.password AS user_password, task.user_id AS user_id, " +
                    "role.role_id AS role_id, role.role AS role, " +
                    "task.name AS task_name, task.description AS task_description, " +
                    "task.priority AS priority, task.date AS date, " +
                    "task.task_id AS task_id, task.stage AS stage, " +
                    "task.accepted AS accepted, task.count_observers AS count_observers " +
                    "FROM task LEFT JOIN users ON task.user_id = users.user_id LEFT JOIN " +
                    "role_user ON task.user_id = role_user.user_id " +
                    "LEFT JOIN role ON role_user.role_id = role.role_id", new TaskMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Task> getPageAvailable(Integer page) {
        try {
            return jdbcTemplate.query("SELECT users.name AS username, users.email AS email, " +
                    "users.password AS user_password, task.user_id AS user_id, " +
                    "role.role_id AS role_id, role.role AS role, " +
                    "task.name AS task_name, task.description AS task_description, " +
                    "task.priority AS priority, task.date AS date, " +
                    "task.task_id AS task_id, task.stage AS stage, " +
                    "task.accepted AS accepted, task.count_observers AS count_observers " +
                    "FROM task LEFT JOIN users ON task.user_id = users.user_id LEFT JOIN " +
                    "role_user ON task.user_id = role_user.user_id " +
                    "LEFT JOIN role ON role_user.role_id = role.role_id  WHERE task.stage = ? " +
                    "ORDER BY task.date DESC LIMIT 15 OFFSET ? ", new TaskMapper(), TaskStage.NOT_TAKEN.toString(), (page - 1) * 15);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<Task> getPage(Integer page) {
        try {
            return jdbcTemplate.query("SELECT users.name AS username, users.email AS email, " +
                    "users.password AS user_password, task.user_id AS user_id, " +
                    "role.role_id AS role_id, role.role AS role, " +
                    "task.name AS task_name, task.description AS task_description, " +
                    "task.priority AS priority, task.date AS date, " +
                    "task.task_id AS task_id, task.stage AS stage, " +
                    "task.accepted AS accepted, task.count_observers AS count_observers " +
                    "FROM task LEFT JOIN users ON task.user_id = users.user_id LEFT JOIN " +
                    "role_user ON task.user_id = role_user.user_id " +
                    "LEFT JOIN role ON role_user.role_id = role.role_id " +
                    "ORDER BY task.date DESC LIMIT 15 OFFSET ?", new TaskMapper(), (page - 1) * 15);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<Task> getPageAvailableByPriority(Integer page) {
        try {
            return jdbcTemplate.query("SELECT users.name AS username, users.email AS email, " +
                    "users.password AS user_password, task.user_id AS user_id, " +
                    "role.role_id AS role_id, role.role AS role, " +
                    "task.name AS task_name, task.description AS task_description, " +
                    "task.priority AS priority, task.date AS date, " +
                    "task.task_id AS task_id, task.stage AS stage, " +
                    "task.accepted AS accepted, task.count_observers AS count_observers " +
                    "FROM task LEFT JOIN users ON task.user_id = users.user_id LEFT JOIN " +
                    "role_user ON task.user_id = role_user.user_id " +
                    "LEFT JOIN role ON role_user.role_id = role.role_id WHERE task.stage = ? " +
                    "ORDER BY task.priority DESC LIMIT 15 OFFSET ?", new TaskMapper(), TaskStage.NOT_TAKEN.toString(), (page - 1) * 15);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<Task> getPageByPriority(Integer page) {
        try {
            return jdbcTemplate.query("SELECT users.name AS username, users.email AS email, " +
                    "users.password AS user_password, task.user_id AS user_id, " +
                    "role.role_id AS role_id, role.role AS role, " +
                    "task.name AS task_name, task.description AS task_description, " +
                    "task.priority AS priority, task.date AS date, " +
                    "task.task_id AS task_id, task.stage AS stage, " +
                    "task.accepted AS accepted, task.count_observers AS count_observers " +
                    "FROM task LEFT JOIN users ON task.user_id = users.user_id LEFT JOIN " +
                    "role_user ON task.user_id = role_user.user_id " +
                    "LEFT JOIN role ON role_user.role_id = role.role_id " +
                    "ORDER BY task.priority DESC LIMIT 15 OFFSET ?", new TaskMapper(), (page - 1) * 15);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public int removeById(int id) {
        return jdbcTemplate.update("DELETE FROM task WHERE task_id=?", id);
    }

    @Override
    public Optional<Task> findByName(String name) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT users.name AS username, users.email AS email, " +
                            "users.password AS user_password, task.user_id AS user_id, " +
                            "role.role_id AS role_id, role.role AS role, " +
                            "task.name AS task_name, task.description AS task_description, " +
                            "task.priority AS priority, task.date AS date, " +
                            "task.task_id AS task_id, task.stage AS stage, " +
                            "task.accepted AS accepted, task.count_observers AS count_observers " +
                            "FROM task LEFT JOIN users ON task.user_id = users.user_id LEFT JOIN " +
                            "role_user ON task.user_id = role_user.user_id " +
                            "LEFT JOIN role ON role_user.role_id = role.role_id WHERE task.name = ?", new TaskMapper(), name)
            );
        } catch (DataAccessException a) {
            log.info("User with name [{}] not exists", name);
            return Optional.empty();
        }
    }

    @Override
    public int removeByName(String name) {
        try {
            int updatedRows = jdbcTemplate.update("DELETE FROM task WHERE name = ?", name);
            log.info("task with name {} removed", name);
            return updatedRows;
        } catch (DataAccessException exception) {
            log.error("Don't remove task with name {}. Exception - {}", name, exception.getMessage());
            return 0;
        }
    }

    @Override
    public List<Task> getByUser(User user) {
        try {
            return jdbcTemplate.query("SELECT users.name AS username, users.email AS email, " +
                    "users.password AS user_password, task.user_id AS user_id, " +
                    "role.role_id AS role_id, role.role AS role, " +
                    "task.name AS task_name, task.description AS task_description, " +
                    "task.priority AS priority, task.date AS date, " +
                    "task.task_id AS task_id, task.stage AS stage, " +
                    "task.accepted AS accepted, task.count_observers AS count_observers " +
                    "FROM task LEFT JOIN users ON task.user_id = users.user_id LEFT JOIN " +
                    "role_user ON task.user_id = role_user.user_id " +
                    "LEFT JOIN role ON role_user.role_id = role.role_id WHERE users.user_id = ?", new TaskMapper(), user.getUserId());
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public int update(Task task) {
        try {
            return jdbcTemplate.update("UPDATE task SET priority = ?, description = ?, " +
                            "user_id = ?, name = ?, date = ?, stage = ?, accepted = ?, " +
                            "count_observers = ? WHERE task_id = ?", task.getPriority(), task.getDescription(),
                    task.getUser() == null ? null : task.getUser().getUserId(), task.getName(), task.getDate(), task.getStage().toString(), task.getAccepted(), task.getCountObservers(), task.getTaskId());
        } catch (DataAccessException e) {
            log.error("Task with id = {} doesn't updated. Exception - {}", task.getTaskId(), e.getMessage());
            return 0;
        }
    }

    @Override
    public void finishTask(int id) {
        int updated = jdbcTemplate.update("UPDATE task SET stage = ? WHERE task_id = ?", TaskStage.COMPLETED.toString(), id);
        if (updated == 0) {
            log.error("task {} not finished", id);
        }
    }

    @Override
    public int addObserver(int taskId, int userId) {
        try {
            int result =  jdbcTemplate.update("INSERT INTO observe (task_id, user_id) VALUES (?, ?)", taskId, userId);
            int count = jdbcTemplate.update("UPDATE task SET count_observers = count_observers + 1 WHERE task_id = ?", taskId);
            if (count != 1) {
                log.error("There is {} updated tasks , when expected 1", count);
            }
            return result;
        } catch (DataAccessException e) {
            log.error("Not added observer. {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean isExist(int id) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM task WHERE task_id = ?", Integer.class, id);
        return count != null && count != 0;
    }

    @Override
    public int deleteCompleted() {
        return jdbcTemplate.update("DELETE FROM task WHERE stage = ?", TaskStage.COMPLETED.toString());
    }

    @Override
    public boolean isUserMonitoring(int taskId, int userId) {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM observe WHERE user_id = ? and task_id = ?", Integer.class, userId, taskId);
            return result != null && result != 0;
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void updateOnlyStage(TaskStage stage, Integer taskId) {
        try {
            jdbcTemplate.update("UPDATE task SET stage = ? WHERE task_id = ?", stage.toString(), taskId);
        } catch (DataAccessException e) {
            log.error("Task stage {} doesn't updated. Exception - {}", taskId, e.getMessage());
        }
    }

    @Override
    public Optional<TaskStage> getStage(Integer taskId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT (stage) FROM task WHERE task_id = ?", new TaskStageMapper(), taskId));
        } catch (DataAccessException e) {
            log.error("unable to get task {} step. Exception - {}", taskId, e.getMessage());
            return Optional.empty();
        }
    }
}
