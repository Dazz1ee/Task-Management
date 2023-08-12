package com.taskmanagement.dao;

import com.taskmanagement.models.Task;
import com.taskmanagement.models.TaskStage;
import com.taskmanagement.models.User;

import java.util.List;
import java.util.Optional;

public interface TaskDao {
    int add(Task task);
    List<Task> getAll();
    List<Task> getPageAvailable(Integer page);
    List<Task> getPage(Integer page);
    Optional<Task> findById(int id);
    int removeById(int id);
    Optional<Task> findByName(String name);
    int removeByName(String name);
    List<Task> getByUser(User user);
    int update(Task task);
    void finishTask(int id);
    int addObserver(int taskId, int userId);
    boolean isExist(int id);
    int deleteCompleted();

    boolean isUserMonitoring(int taskId, int userId);

    List<Task> getPageAvailableByPriority(Integer page);

    List<Task> getPageByPriority(Integer page);

    void updateOnlyStage(TaskStage stage, Integer taskId);

    Optional<TaskStage> getStage(Integer taskId);

}
