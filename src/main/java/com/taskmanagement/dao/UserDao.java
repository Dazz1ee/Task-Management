package com.taskmanagement.dao;

import com.taskmanagement.models.Role;
import com.taskmanagement.models.User;

import java.util.Optional;

public interface UserDao {

    Optional<User> findUserById(int id);

    Optional<User> findUserByEmail(String email);

    int saveUser(User user);

    int deleteUserById(int id);

    int deleteUserByEmail(String email);

    boolean addRole(User user, Role role);
}
