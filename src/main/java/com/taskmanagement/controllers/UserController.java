package com.taskmanagement.controllers;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import com.taskmanagement.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final RoleService roleService;

    @PostMapping("/add-moder")
    public ResponseEntity<?> addModer(@RequestBody String email) {
        if (roleService.addRoleModer(email)) {
            return ResponseEntity.ok("User became a moder");
        }
        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Role not added");
    }
}
