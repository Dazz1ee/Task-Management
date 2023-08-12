//package com.example.filemanager.services;
//
//import com.example.filemanager.dao.UserDao;
//import org.checkerframework.checker.units.qual.A;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//class AuthenticationServiceTest {
//
//    @Mock
//    private UserDao userDao;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//    @Mock
//    private JwtService jwtService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private
//
//    private AuthenticationService authenticationService;
//
//    @BeforeEach
//    private void initialization(){
//        authenticationService = new AuthenticationService(userDao, passwordEncoder, jwtService, authenticationManager);
//    }
//
//
//    @Test
//    void signUp() {
//    }
//
//    @Test
//    void signIn() {
//    }
//}