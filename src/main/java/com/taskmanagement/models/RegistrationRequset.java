package com.taskmanagement.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record RegistrationRequset(
        @Size(min = 4, max = 30)
        String name,
        @Email String email,
        @Size(min = 4, max = 30) String password) {
}
