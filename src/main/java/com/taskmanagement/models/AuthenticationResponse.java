package com.taskmanagement.models;

public record AuthenticationResponse(String accessToken, String refreshToken) {
}
