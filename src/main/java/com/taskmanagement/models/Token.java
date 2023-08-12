package com.taskmanagement.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private Integer tokenId;

    private Integer userId;

    private String token;

    private boolean revoked;

    private boolean expired;

    public Token(Integer userId, String token, boolean revoked, boolean expired) {
        this.userId = userId;
        this.token = token;
        this.revoked = revoked;
        this.expired = expired;
    }



}
