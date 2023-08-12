package com.taskmanagement.services;

import com.taskmanagement.dao.TaskDao;
import com.taskmanagement.exceptions.TaskNotFoundException;
import com.taskmanagement.models.*;
import net.sourceforge.argparse4j.annotation.Arg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskDao taskDao;
    private TaskService taskService;

    @BeforeEach
    public void createTaskService() {
        taskService = new TaskService(taskDao);
    }


    @Test
    @Disabled
    void updateStage() {
    }

    @Test
    @Disabled
    void observeTask() {
    }

    @Test
    @Disabled
    void newTask() {

    }

    @Test
    void getPageTasksNotTaken() {
        //given
        int page = 1;

        //when
        taskService.getPageTasksNotTaken(page);

        //then
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(taskDao).getPageAvailable(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(page);

    }

    @Test
    void getPageTasksNotTakenPriorityOrder() {
        //given
        int page = 1;

        //when
        taskService.getPageTasksNotTakenPriorityOrder(page);

        //then
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(taskDao).getPageAvailableByPriority(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(page);
    }

    @Test
    void getPageTasks() {
        //given
        int page = 1;

        //when
        taskService.getPageTasks(page);

        //then
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(taskDao).getPage(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(page);
    }

    @Test
    void getPageTasksPriorityOrder() {
        //given
        int page = 1;

        //when
        taskService.getPageTasksPriorityOrder(page);

        //then
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(taskDao).getPageByPriority(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(page);
    }

    @Test
    void takeNotTakenTask() {
        //given
        int taskId = 1;

        User user = User.builder()
                        .userId(any())
                        .email("test@gmail.com")
                        .name("test")
                        .password("test")
                        .roles(List.of(new Role(3, RoleEnum.ROLE_MODER)))
                        .build();

        Task task = Task.builder()
                .taskId(taskId)
                .user(null)
                .name("task")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.NOT_TAKEN)
                .build();

        when(taskDao.findById(taskId)).thenReturn(Optional.ofNullable(task));
        when(taskDao.update(task)).thenReturn(1);

        //when
        boolean status = taskService.takeTask(taskId, user);

        Objects.requireNonNull(task).setUser(user);
        task.setStage(TaskStage.IN_PROGRESS);


        //then
        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDao).update(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(task);
        assertThat(status).isTrue();
    }

    @Test
    void takeAlreadyTakenTask() {
        //given
        int taskId = 1;

        User user = User.builder()
                .userId(any())
                .email("test@gmail.com")
                .name("test")
                .password("test")
                .roles(List.of(new Role(3, RoleEnum.ROLE_MODER)))
                .build();

        Task task = Task.builder()
                .taskId(taskId)
                .user(user)
                .name("task")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.NOT_TAKEN)
                .build();

        when(taskDao.findById(taskId)).thenReturn(Optional.ofNullable(task));


        //when
        boolean status = taskService.takeTask(taskId, user);

        Objects.requireNonNull(task).setUser(user);
        task.setStage(TaskStage.IN_PROGRESS);

        assertThat(status).isFalse();
    }

    @Test
    void getUsersTask() {
        //given
        User user = User.builder()
                .userId(1)
                .name("test")
                .email("test@gmail.com")
                .password("test")
                .roles(List.of(new Role(3, RoleEnum.ROLE_USER)))
                .build();

        //when
        taskService.getUsersTask(user);

        //then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(taskDao).getByUser(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(user);
    }

    @Test
    void getTaskExists() {
        //given
        int taskId = 1;
        Task task = Task.builder()
                        .taskId(taskId)
                        .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                        .priority(1)
                        .name("test")
                        .build();

        when(taskDao.findById(taskId)).thenReturn(Optional.of(task));

        //when
        Task receivedTask = taskService.getTask(taskId);


        assertThat(receivedTask).isEqualTo(task);
    }

    @Test
    void getTaskNotExists() {
        //given
        int taskId = 1;

        //when
        assertThatThrownBy(() -> taskService.getTask(taskId)).isInstanceOf(TaskNotFoundException.class).hasMessage(String.format("task [id  %d] not exists", taskId));

    }

    @Test
    void deleteCompletedTask() {
        //when
        taskService.deleteCompletedTask();

        //then
        verify(taskDao).deleteCompleted();
    }

    @Test
    void isExists() {
        //given
        int id = 1;

        //when
        taskService.isExists(id);

        //then
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(taskDao).isExist(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(id);
    }

    @Test
    void isUsersMonitoringTask() {
        //given
        int taskId = 1;
        int userId = 1;

        //when
        taskService.isUsersMonitoringTask(taskId, userId);

        //then
        ArgumentCaptor<Integer> taskCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> userCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(taskDao).isUserMonitoring(taskCaptor.capture(), userCaptor.capture());

        assertThat(taskCaptor.getValue()).isEqualTo(taskId);
        assertThat(userCaptor.getValue()).isEqualTo(userId);
    }

    @Test
    void isCompleted() {
        //given
        int id = 1;
        Task task = Task.builder()
                .stage(TaskStage.COMPLETED)
                .build();
        when(taskDao.getStage(id)).thenReturn(Optional.of(TaskStage.COMPLETED));

        //when
        taskService.isCompleted(id);

        //then
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(taskDao).getStage(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(id);
    }

    @Test
    void isCompletedNotExists() {
        //given
        int id = 1;

        //then
        assertThatThrownBy(() -> taskService.isCompleted(id)).isInstanceOf(TaskNotFoundException.class);
    }
}