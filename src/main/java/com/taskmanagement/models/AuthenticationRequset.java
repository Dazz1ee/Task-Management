package com.taskmanagement.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AuthenticationRequset(
        @Email String email,
        @Size(min = 4, max = 30) String password) {
}
