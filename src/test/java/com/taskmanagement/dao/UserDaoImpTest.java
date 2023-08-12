package com.taskmanagement.dao;

//import com.example.filemanager.ContainerEnvironment;
import com.taskmanagement.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserDaoImpTest{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDao userDao;


    @Autowired
    private RoleDao roleDao;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest");

    @AfterEach
    public void deleteEntity(){
        jdbcTemplate.update("DELETE FROM users");
    }


    @Test
    void saveUserThatDoesNotExist() {
        //given
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));

        //when
        int response = userDao.saveUser(user);

        //then
        assertThat(response).isEqualTo(user.getUserId());
    }

    @Test
    void saveUserThatExists() {
        //given
        User user1 = new User("test1", "test@gg.com", "test");
        User user2 = new User("test2", "test@gg.com", "test");
        user1.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        user2.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        userDao.saveUser(user1);

        //when
        int response = userDao.saveUser(user2);

        //then
        assertThat(response).isEqualTo(0);
    }

    @Test
    void findUserByIdIfExists() {
        //given
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        int id = userDao.saveUser(user);
        System.out.println(id);

        //when
        Optional<User> expected = userDao.findUserById(id);

        //then
        assertThat(expected).isPresent();
        assertThat(expected.get()).isEqualTo(user);
    }

    @Test
    void findUserByIdIfNotExists() {
        //when
        Optional<User> expected = userDao.findUserById(13113);

        //then
        assertThat(expected).isEmpty();
    }

    @Test
    void findUserByEmailIfExists() {
        //given
        String email = "test@gg.com";
        User user = new User("test", email, "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));

        user.setUserId(userDao.saveUser(user));

        //when
        Optional<User> expected = userDao.findUserByEmail(email);

        //then
        assertThat(expected).isNotEmpty();
        assertThat(expected.get()).isEqualTo(user);
    }

    @Test
    void findUserByEmailIfNotExists() {
        //when
        Optional<User> expected = userDao.findUserByEmail("dadasd@gg.com");

        //then
        assertThat(expected).isEmpty();
    }


    @Test
    void deleteUserByIdIfExists() {
        //given
        User user = new User("test", "test@gg.com", "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        userDao.saveUser(user);

        //when
        int expected = userDao.deleteUserById(user.getUserId());

        //then
        assertThat(expected).isEqualTo(1);
    }

    @Test
    void deleteUserByIdIfNotExists() {
        //when
        int expected = userDao.deleteUserById(5);
        //then
        assertThat(expected).isEqualTo(0);
    }

    @Test
    void deleteUserByEmailIfExists() {
        //given
        String email = "test@gg.com";
        User user = new User("test", email, "test");
        user.getRoles().add(roleDao.getRoleByName("ROLE_USER"));
        userDao.saveUser(user);

        //when
        int expected = userDao.deleteUserByEmail(email);

        //then
        assertThat(expected).isEqualTo(1);
    }


    @Test
    void deleteUserByEmailIfNotExists() {
        //given
        String email = "test@gg.com";

        //when
        int expected = userDao.deleteUserByEmail(email);

        //then
        assertThat(expected).isEqualTo(0);
    }

}