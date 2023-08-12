package com.taskmanagement.services;

import com.taskmanagement.dao.UserDaoImp;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDaoImp userDaoImp;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDaoImp.findUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User with " + username + " not found"));
    }
}
