package com.taskmanagement.services;

import com.taskmanagement.dao.ReviewDao;
import com.taskmanagement.dao.TaskDao;
import com.taskmanagement.exceptions.TaskNotFoundException;
import com.taskmanagement.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@KafkaListener(id = "taskGroup", topics = {"newTask", "observe", "update"}, containerFactory = "kafkaListenerContainerFactory")
public class TaskService {
    private final TaskDao taskDao;

    @KafkaHandler
    public void updateStage(Integer taskId) {
        Optional<TaskStage> stageOptional = taskDao.getStage(taskId);
        if (stageOptional.isPresent()) {
            switch (stageOptional.get()) {
                case IN_PROGRESS -> taskDao.updateOnlyStage(TaskStage.WAIT_REVIEW, taskId);
                default -> log.warn("unable to update stage");
            }

        }
    }

    @KafkaHandler
    public void observeTask(MessageObserver messageObserver) {
        log.info("Task {} monitoring", messageObserver.task_id());
        if (taskDao.isExist(messageObserver.task_id())) {
            if (taskDao.addObserver(messageObserver.task_id(), messageObserver.user_id()) == 0) {
                log.warn("The user {} failed to take the task {}", messageObserver.user_id(), messageObserver.task_id());
            }
        }
    }

    @KafkaHandler
    public void newTask (TaskRequest request) {
        Task task = Task.builder()
                .name(request.name())
                .stage(TaskStage.NOT_TAKEN)
                .date(LocalDateTime.now().truncatedTo(TimeUnit.MINUTES.toChronoUnit()))
                .priority(request.priority())
                .countObservers(0)
                .accepted(0)
                .description(request.description())
                .build();

        int isSaved = taskDao.add(task);
        if (isSaved == 0) {
            log.error("Task with name [{}] not added", task);
        } else {
            log.info("Task with name [{}] added", task);
        }
    }

    public List<Task> getPageTasksNotTaken(Integer numberPage) {
        return taskDao.getPageAvailable(numberPage);
    }

    public List<Task> getPageTasksNotTakenPriorityOrder(Integer numberPage) {
        return taskDao.getPageAvailableByPriority(numberPage);
    }

    public List<Task> getPageTasks(Integer numberPage) {
        return taskDao.getPage(numberPage);

    }

    public List<Task> getPageTasksPriorityOrder(Integer numberPage) {
        return taskDao.getPageByPriority(numberPage);

    }

    public boolean takeTask(Integer id, User user) {
        Task task = taskDao.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        if (task.getUser() != null) {
            log.warn("task [id  {}] is unavailable", task.getTaskId());
            return false;
        } else {
            task.setUser(user);
            task.setStage(TaskStage.IN_PROGRESS);

            if (taskDao.update(task) == 0) {
                log.error("The user, {}, failed to take the task [{}]", user.getUsername(), task.getName());
                return false;
            }

            log.info("The user, {}, took the task [{}]", user.getUsername(), task.getName());
            return true;
        }
    }

    public List<Task> getUsersTask(User user) {
        return taskDao.getByUser(user);
    }

    public Task getTask(Integer id) {
        return taskDao.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public int deleteCompletedTask() {
        return taskDao.deleteCompleted();
    }

    public boolean isExists(int id) {
        return taskDao.isExist(id);
    }

    public boolean isUsersMonitoringTask(int taskId, int userId) {
        return taskDao.isUserMonitoring(taskId, userId);
    }

    public boolean isCompleted(Integer id) {
        TaskStage taskStage = taskDao.getStage(id).orElseThrow(() -> new TaskNotFoundException("Task hasn't stage or task not exists"));
        return taskStage.equals(TaskStage.COMPLETED);
    }
}
