package com.taskmanagement.configurations;

import com.taskmanagement.dao.JwtDao;
import com.taskmanagement.models.Token;
import com.taskmanagement.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogoutHandlerImp implements LogoutHandler {

    private final JwtDao jwtDao;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info(String.format("%s logout", user.getName()));
        String header = request.getHeader("Authorization");
        if (header == null ||!header.startsWith("Bearer ")) {
            return;
        }
        String jwt = header.substring(7);
        Token storedToken = jwtDao.findByToken(jwt)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            jwtDao.update(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
}
