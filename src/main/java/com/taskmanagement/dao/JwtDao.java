package com.taskmanagement.dao;

import com.taskmanagement.models.Token;

import java.util.List;
import java.util.Optional;

public interface JwtDao {

    Optional<Token> findByToken(String token);
    List<Token> findValidTokensForUser(Integer id);

    int saveToken(Token token);

    boolean updateAll(List<Token> tokens);

    int update(Token token);

    int deleteTokenByToken(Token token);

    int deleteTokenById(Integer id);

}

