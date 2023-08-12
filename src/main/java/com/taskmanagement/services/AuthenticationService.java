package com.taskmanagement.services;

import com.taskmanagement.dao.JwtDao;
import com.taskmanagement.dao.RoleDao;
import com.taskmanagement.dao.UserDao;
import com.taskmanagement.exceptions.TokenRefreshException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanagement.models.*;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtDao jwtDao;
    private final RoleDao roleDao;

    public AuthenticationResponse signUp(RegistrationRequset request) {
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(List.of(roleDao.getRoleByName(RoleEnum.ROLE_USER.toString())))
                .build();
        if (userDao.saveUser(user) == 0) {
            throw new BadCredentialsException("This email is used");
        }
        String accessToken = jwtService.generateToken(new HashMap<>(), user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, accessToken);
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse signIn(AuthenticationRequset request) {
        User user = userDao.findUserByEmail(request.email()).orElseThrow(() -> new BadCredentialsException("Not correct password or email"));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        String accessToken = jwtService.generateToken(new HashMap<>(), user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    private void saveUserToken(User user, String jwt) {
        Token token = new Token(user.getUserId(), jwt, false, false);
        jwtDao.saveToken(token);
    }

    private void revokeAllUserTokens(User user){
        List<Token> tokens = jwtDao.findValidTokensForUser(user.getUserId());
        if (tokens.isEmpty()) {
            return;
        }

        tokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        jwtDao.updateAll(tokens);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return;
        }
        String refreshToken = header.substring(7);
        try {
            if (jwtService.isTokenExpired(refreshToken)) {
                response.sendRedirect("/logout");
            }
        } catch (IOException | JwtException e) {
            throw new TokenRefreshException(refreshToken, "Sign in");
        }
        String email = jwtService.extractUsername(refreshToken);
        if (email == null) {
            return;
        }

        User user = userDao.findUserByEmail(email).orElseThrow(() -> new BadCredentialsException("User not found"));
        if (jwtService.isTokenValid(refreshToken, user)) {
            String accessToken = jwtService.generateToken(new HashMap<>(), user);
            revokeAllUserTokens(user);
            saveUserToken(user, accessToken);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse(accessToken, refreshToken);
            try {
                new ObjectMapper().writeValue(response.getOutputStream(), authenticationResponse);
            } catch (IOException e) {
                log.error("Can't get outputStream from response");
                System.out.println("???");
            }
        }
    }
}
