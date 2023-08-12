package com.taskmanagement.dao;

import com.taskmanagement.mappers.TaskStageMapper;
import com.taskmanagement.models.Task;
import com.taskmanagement.models.TaskStage;
import com.taskmanagement.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import scala.Int;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TaskDaoImpTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest");

    @AfterEach
    public void clear() {
        jdbcTemplate.update("DELETE FROM task");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void addWithoutUser() {

        //given
        Task task = Task.builder()
                .priority(1)
                .description("test")
                .name("test")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.NOT_TAKEN)
                .build();

        //when
        int id = taskDao.add(task);

        assertThat(id).isNotEqualTo(0).isEqualTo(task.getTaskId());
    }

    @Test
    void addWithUser() {
        User user = User.builder()
                .password("test")
                .name("test")
                .roles(List.of(roleDao.getRoleByName("ROLE_USER")))
                .email("test@ggg.com")
                .build();
        userDao.saveUser(user);

        //given
        Task task = Task.builder()
                .priority(1)
                .description("test")
                .name("test")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .user(user)
                .stage(TaskStage.IN_PROGRESS)
                .build();

        //when
        int id = taskDao.add(task);

        //then
        assertThat(id).isNotEqualTo(0).isEqualTo(task.getTaskId());
    }

    @Test
    void findByIdIfUserExist() {
        //given
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        int res = userDao.saveUser(user);
        System.err.println(res);
        Task task = Task.builder()
                .priority(1)
                .description("test")
                .name("test")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .user(user)
                .stage(TaskStage.IN_PROGRESS)
                .build();

        int id = taskDao.add(task);

        //when
        Optional<Task> expected = taskDao.findById(id);
        //then
        assertThat(expected).isNotEmpty().contains(task);
    }

    @Test
    void findByIdIfUserNotExist() {
        Task task = Task.builder()
                .priority(1)
                .description("test")
                .name("test")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.NOT_TAKEN)
                .build();

        int id = taskDao.add(task);

        //when
        Optional<Task> expected = taskDao.findById(id);

        //then
        assertThat(expected).isNotEmpty().contains(task);
    }

    @Test
    void findByIdIfNotExist() {

        //when
        Optional<Task> expected = taskDao.findById(-1);

        //then
        assertThat(expected).isEmpty();
    }


    @Test
    void getAll() {
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        int res = userDao.saveUser(user);
        System.err.println(res);
        Task task1 = Task.builder()
                .priority(1)
                .description("test1")
                .name("test")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .user(user)
                .stage(TaskStage.IN_PROGRESS)
                .build();

        Task task2 = Task.builder()
                .priority(1)
                .description("test2")
                .name("test")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.NOT_TAKEN)
                .build();

        taskDao.add(task1);
        taskDao.add(task2);

        //when
        List<Task> tasks = taskDao.getAll();

        //then
        assertThat(tasks).asList().containsOnly(task1, task2);
    }

    @Test
    void removeByIdIfExist() {
        //given
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        int res = userDao.saveUser(user);
        System.err.println(res);
        Task task = Task.builder()
                .priority(1)
                .description("test")
                .name("test")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .user(user)
                .stage(TaskStage.IN_PROGRESS)
                .build();
        int id = taskDao.add(task);

        //when
        int response = taskDao.removeById(id);

        //then
        assertThat(response).isEqualTo(1);
    }


    @Test
    void removeByIdIfNotExist() {
        //when
        int response = taskDao.removeById(1232131);

        //then
        assertThat(response).isEqualTo(0);
    }

    @Test
    void findByNameIfExist() {
        //given
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        int res = userDao.saveUser(user);

        Task task = Task.builder()
                .priority(1)
                .description("test")
                .name("test")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .user(user)
                .stage(TaskStage.IN_PROGRESS)
                .build();

        int id = taskDao.add(task);

        //when
        Optional<Task> expected = taskDao.findByName("test");
        //then
        assertThat(expected).isNotEmpty().contains(task);
    }

    @Test
    void findByNameIfNotExist() {
        //when
        Optional<Task> expected = taskDao.findByName("test");
        //then
        assertThat(expected).isEmpty();
    }


    @Test
    void removeByNameIfExist() {
        //given
        Task task = Task.builder()
                .priority(1)
                .description("test")
                .name("test")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.NOT_TAKEN)
                .build();

        int id = taskDao.add(task);

        //when
        int expected = taskDao.removeByName("test");
        //then
        assertThat(expected).isEqualTo(1);

    }


    @Test
    void removeByNameIfNotExist() {
        //when
        int expected = taskDao.removeByName("test");
        //then
        assertThat(expected).isEqualTo(0);

    }

    @Test
    void getByUser() {
        //given
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        int res = userDao.saveUser(user);
        Task task1 = Task.builder()
                .priority(1)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .user(user)
                .stage(TaskStage.IN_PROGRESS)
                .build();

        Task task2 = Task.builder()
                .priority(1)
                .description("test")
                .name("test2")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .user(user)
                .stage(TaskStage.IN_PROGRESS)
                .build();

        taskDao.add(task1);
        taskDao.add(task2);

        //when
        List<Task> expected = taskDao.getByUser(user);

        //then
        assertThat(expected).asList().containsOnly(task1, task2);
    }

    @Test
    void update() {
        //given
        User user = new User("test@ggg.com", "test", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        userDao.saveUser(user);

        Task task = Task.builder()
                .priority(1)
                .description("test")
                .name("test")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.NOT_TAKEN)
                .build();
        int id = taskDao.add(task);

        //when
        task.setUser(user);
        task.setStage(TaskStage.IN_PROGRESS);

        int expected = taskDao.update(task);

        //then
        assertThat(expected).isEqualTo(1);
        assertThat(task).isEqualTo(taskDao.findById(task.getTaskId()).orElse(null));
    }


    @Test
    void updateTaskNotExists() {
        //given;

        Task task = Task.builder()
                .priority(1)
                .description("test")
                .name("test")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.NOT_TAKEN)
                .build();

        int expected = taskDao.update(task);

        //then;
        assertThat(expected).isEqualTo(0);
    }

    @Test
    void getAvailable() {
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        int res = userDao.saveUser(user);

        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .user(user)
                .stage(TaskStage.IN_PROGRESS)
                .build();

        taskDao.add(task);
        task.setUser(null);
        task.setStage(TaskStage.NOT_TAKEN);

        for (int i = 0; i < 25; i++) {
            taskDao.add(task);
        }

        task.setUser(user);
        task.setStage(TaskStage.IN_PROGRESS);
        taskDao.add(task);

        //when
        List<Task> expectedFirstPage = taskDao.getPageAvailable(1);
        List<Task> expectedSecondPage = taskDao.getPageAvailable(2);

        //then
        assertThat(expectedFirstPage.size()).isEqualTo(15);
        assertThat(expectedSecondPage.size()).isEqualTo(10);

        HashMap<Integer, Boolean> map = new HashMap<>();

        for (Task temp : expectedFirstPage) {
            assertThat(map.containsKey(temp.getTaskId())).isFalse();
            assertThat(temp.getUser()).isNull();
            map.put(temp.getTaskId(), true);
        }

        for (Task temp : expectedSecondPage) {
            assertThat(map.containsKey(temp.getTaskId())).isFalse();
            assertThat(temp.getUser()).isNull();
            map.put(temp.getTaskId(), true);
        }
    }

    @Test
    void getAvailableByPriority() {
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));


        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .user(user)
                .stage(TaskStage.IN_PROGRESS)
                .build();

        taskDao.add(task);
        task.setUser(null);
        task.setStage(TaskStage.NOT_TAKEN);

        for (int i = 0; i < 25; i++) {
            task.setPriority(i % 5 + 1);
            taskDao.add(task);
        }

        task.setUser(user);
        task.setStage(TaskStage.IN_PROGRESS);
        taskDao.add(task);

        //when
        List<Task> expectedFirstPage = taskDao.getPageAvailableByPriority(1);
        List<Task> expectedSecondPage = taskDao.getPageAvailableByPriority(2);

        //then
        assertThat(expectedFirstPage.size()).isEqualTo(15);
        assertThat(expectedSecondPage.size()).isEqualTo(10);

        HashMap<Integer, Boolean> map = new HashMap<>();

        int prev = 5;
        for (Task temp : expectedFirstPage) {
            assertThat(prev).isGreaterThanOrEqualTo(temp.getPriority());
            assertThat(map.containsKey(temp.getTaskId())).isFalse();
            assertThat(temp.getUser()).isNull();
            map.put(temp.getTaskId(), true);
            prev = temp.getPriority();
        }

        for (Task temp : expectedSecondPage) {
            assertThat(prev).isGreaterThanOrEqualTo(temp.getPriority());
            assertThat(map.containsKey(temp.getTaskId())).isFalse();
            assertThat(temp.getUser()).isNull();
            map.put(temp.getTaskId(), true);
            prev = temp.getPriority();
        }
    }


    @Test
    void getAllByPage() {
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        int res = userDao.saveUser(user);

        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.IN_PROGRESS)
                .user(user)
                .build();

        taskDao.add(task);
        task.setUser(null);
        task.setStage(TaskStage.NOT_TAKEN);

        for (int i = 0; i < 25; i++) {
            taskDao.add(task);
        }


        //when
        List<Task> expectedFirstPage = taskDao.getPage(1);
        List<Task> expectedSecondPage = taskDao.getPage(2);

        //then
        assertThat(expectedFirstPage.size()).isEqualTo(15);
        assertThat(expectedSecondPage.size()).isEqualTo(11);

        HashMap<Integer, Boolean> map = new HashMap<>();

        for (Task temp : expectedFirstPage) {
            assertThat(map.containsKey(temp.getTaskId())).isFalse();
            map.put(temp.getTaskId(), true);
        }

        for (Task temp : expectedSecondPage) {
            assertThat(map.containsKey(temp.getTaskId())).isFalse();
            map.put(temp.getTaskId(), true);
        }
    }

    @Test
    void getAllByPriority() {
        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.IN_PROGRESS)
                .build();

        task.setUser(null);
        task.setStage(TaskStage.NOT_TAKEN);

        for (int i = 0; i < 25; i++) {
            task.setPriority(i % 5 + 1);
            taskDao.add(task);
        }


        //when
        List<Task> expectedFirstPage = taskDao.getPageByPriority(1);
        List<Task> expectedSecondPage = taskDao.getPageByPriority(2);

        //then
        assertThat(expectedFirstPage.size()).isEqualTo(15);
        assertThat(expectedSecondPage.size()).isEqualTo(10);

        HashMap<Integer, Boolean> map = new HashMap<>();

        int prevPriority = 5;

        for (Task temp : expectedFirstPage) {
            assertThat(prevPriority).isGreaterThanOrEqualTo(temp.getPriority());
            assertThat(map.containsKey(temp.getTaskId())).isFalse();
            map.put(temp.getTaskId(), true);
            prevPriority = temp.getPriority();
        }


        for (Task temp : expectedSecondPage) {
            assertThat(prevPriority).isGreaterThanOrEqualTo(temp.getPriority());
            assertThat(map.containsKey(temp.getTaskId())).isFalse();
            map.put(temp.getTaskId(), true);
            prevPriority = temp.getPriority();
        }
    }


    @Test
    public void finishTaskTest() {
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));

        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.WAIT_REVIEW)
                .user(user)
                .accepted(0)
                .countObservers(0)
                .build();

        //when;
        taskDao.add(task);
        taskDao.finishTask(task.getTaskId());

        //then
        Optional<Task> result = taskDao.findById(task.getTaskId());
        assertThat(result).isPresent();
        assertThat(result.get().getStage()).isEqualTo(TaskStage.COMPLETED);
    }

    @Test
    public void addObserverSuccess() {
        //given
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));

        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.IN_PROGRESS)
                .user(user)
                .build();

        userDao.saveUser(user);
        taskDao.add(task);

        //when
        int expected = taskDao.addObserver(task.getTaskId(), user.getUserId());
        Optional<Task> result = taskDao.findById(task.getTaskId());
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM observe WHERE task_id = ? and user_id = ?", Integer.class, task.getTaskId(), user.getUserId());

        //that
        assertThat(expected).isEqualTo(1);
        assertThat(count).isEqualTo(1);
        assertThat(result).isPresent();
        assertThat(result.get().getCountObservers()).isEqualTo(1);
    }

    @Test
    public void addObserverFailed() {
        //given
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));

        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.IN_PROGRESS)
                .user(user)
                .build();

        userDao.saveUser(user);
        taskDao.add(task);
        taskDao.addObserver(task.getTaskId(), user.getUserId());

        //when
        int expected = taskDao.addObserver(task.getTaskId(), user.getUserId());

        //that
        assertThat(expected).isEqualTo(0);
    }

    @Test
    public void isExistTrue () {
        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.NOT_TAKEN)
                .build();

        int id = taskDao.add(task);

        //when
        boolean expected = taskDao.isExist(id);

        //then
        assertThat(expected).isTrue();
    }

    @Test
    public void isExistFalse() {

        boolean expected = taskDao.isExist(31313131);

        //then
        assertThat(expected).isFalse();
    }

    @Test
    public void deleteCompletedExists() {
        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.COMPLETED)
                .build();

        int id = taskDao.add(task);

        //when
        int expected = taskDao.deleteCompleted();

        //then
        assertThat(expected).isEqualTo(1);
    }

    @Test
    public void deleteCompletedNotExists() {
        //when
        int expected = taskDao.deleteCompleted();

        //then
        assertThat(expected).isEqualTo(0);
    }

    @Test
    public void isUserMonitoringTest() {
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));

        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.IN_PROGRESS)
                .user(user)
                .build();

        userDao.saveUser(user);
        taskDao.add(task);
        taskDao.addObserver(task.getTaskId(), user.getUserId());

        int otherId = (int) (Math.random() * 100);
        while (otherId == task.getTaskId()) {
            otherId = (int) (Math.random() * 100);
        }

        boolean expectedTrue = taskDao.isUserMonitoring(task.getTaskId(), user.getUserId());
        boolean expectedFalse = taskDao.isUserMonitoring(otherId, user.getUserId());

        assertThat(expectedTrue).isTrue();
        assertThat(expectedFalse).isFalse();

    }

    @Test
    public void updateOnlyStageTest() {
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));

        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.IN_PROGRESS)
                .user(user)
                .build();

        userDao.saveUser(user);
        taskDao.add(task);

        taskDao.updateOnlyStage(TaskStage.WAIT_REVIEW, task.getTaskId());

        assertThat(taskDao.findById(task.getTaskId())).isPresent();
        assertThat(taskDao.findById(task.getTaskId()).get().getStage()).isEqualTo(TaskStage.WAIT_REVIEW);
    }

    @Test
    public void getStageExists() {
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));

        Task task = Task.builder()
                .priority(4)
                .description("test")
                .name("test1")
                .date(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stage(TaskStage.IN_PROGRESS)
                .user(user)
                .build();

        userDao.saveUser(user);
        taskDao.add(task);

        Optional<TaskStage> stage = taskDao.getStage(task.getTaskId());

        assertThat(stage).isPresent();
        assertThat(stage.get()).isEqualTo(TaskStage.IN_PROGRESS);

    }


    @Test
    public void getStageNotExists() {
        Optional<TaskStage> stage = taskDao.getStage(1);
        assertThat(stage).isEmpty();

    }
}


