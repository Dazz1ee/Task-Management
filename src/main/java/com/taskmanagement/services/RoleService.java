package com.taskmanagement.services;

import com.taskmanagement.dao.RoleDao;
import com.taskmanagement.dao.UserDao;
import com.taskmanagement.exceptions.UserNotFoundException;
import com.taskmanagement.models.Role;
import com.taskmanagement.models.RoleEnum;
import com.taskmanagement.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleDao roleDao;
    private final UserDao userDao;

    public boolean addRoleModer(String email) {
        User user = userDao.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(email, "on added role to user"));
        Role role = roleDao.getRoleByName(RoleEnum.ROLE_MODER.toString());

        return userDao.addRole(user, role);
    }
}
