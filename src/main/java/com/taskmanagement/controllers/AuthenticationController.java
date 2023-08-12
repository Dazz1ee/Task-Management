package com.taskmanagement.controllers;

import com.taskmanagement.models.AuthenticationRequset;
import com.taskmanagement.models.AuthenticationResponse;
import com.taskmanagement.models.RegistrationRequset;
import com.taskmanagement.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> signUp(@RequestBody @Valid RegistrationRequset request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder allErrors = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                allErrors.append(error.getDefaultMessage());
            });
            throw new BadCredentialsException(allErrors.toString());
        }
        return ResponseEntity.ok(authenticationService.signUp(request));
    }


    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signIn(@RequestBody @Valid AuthenticationRequset request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder allErrors = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> allErrors.append(error.getDefaultMessage()));
            throw new BadCredentialsException(allErrors.toString());
        }
        return ResponseEntity.ok(authenticationService.signIn(request));
    }

    @PostMapping("/refresh-token")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.refreshToken(request, response);
    }

}
