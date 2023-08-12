package com.taskmanagement.models;

import com.taskmanagement.exceptions.RoleNotFoundException;

import java.util.Arrays;

public enum RoleEnum {
    ROLE_USER {
        @Override
        public String toString() {
            return "ROLE_USER";
        }
    },
    ROLE_MODER {
        @Override
        public String toString() {
            return "ROLE_MODER";
        }
    },
    ROLE_ADMIN {
        @Override
        public String toString() {
            return "ROLE_ADMIN";
        }
    };

    public static RoleEnum findByName(final String role){
        return Arrays.stream(values()).filter(value -> value.toString().equals(role))
                .findFirst().orElseThrow(() -> new RoleNotFoundException(role));
    }
}
