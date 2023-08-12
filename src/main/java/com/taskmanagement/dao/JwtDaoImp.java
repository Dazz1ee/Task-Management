package com.taskmanagement.dao;

import com.taskmanagement.models.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JwtDaoImp implements JwtDao{

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertIntoToken;

    @Autowired
    public JwtDaoImp(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        insertIntoToken = new SimpleJdbcInsert(jdbcTemplate).withTableName("token").usingGeneratedKeyColumns("token_id");
    }

    @Override
    public Optional<Token> findByToken(String token) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM token WHERE token = ?", new BeanPropertyRowMapper<>(Token.class), token));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Token> findValidTokensForUser(Integer id) {
        return jdbcTemplate.query("SELECT * FROM token WHERE user_id = ? and (expired = false or revoked = false)", new BeanPropertyRowMapper<>(Token.class), id);
    }

    @Override
    public int update(Token token) {
        return jdbcTemplate.update("UPDATE token SET expired = ?, revoked = ? WHERE token_id = ?",
                token.isExpired(), token.isRevoked(), token.getTokenId());
    }

    @Override
    public boolean updateAll(List<Token> tokens) {
        try {
            jdbcTemplate.batchUpdate("UPDATE token SET expired = ?, revoked = ? WHERE token_id = ?", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setBoolean(1, tokens.get(i).isExpired());
                    ps.setBoolean(2, tokens.get(i).isRevoked());
                    ps.setInt(3, tokens.get(i).getTokenId());
                }

                @Override
                public int getBatchSize() {
                    return tokens.size();
                }
            });
        } catch (DataAccessException exception) {
            return false;
        }
        return true;
    }

    @Override
    public int saveToken(Token token) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", token.getUserId());
        parameters.put("token", token.getToken());
        parameters.put("revoked", token.isRevoked());
        parameters.put("expired", token.isExpired());

        try {
            token.setTokenId(insertIntoToken.executeAndReturnKey(parameters).intValue());
            return token.getTokenId();
        } catch (DuplicateKeyException e) {
            return 0;
        }
    }

    @Override
    public int deleteTokenByToken(Token token) {
        return jdbcTemplate.update("DELETE FROM token WHERE token = ?", token);
    }

    @Override
    public int deleteTokenById(Integer id) {
        return jdbcTemplate.update("DELETE FROM token WHERE token_id = ?", id);
    }
}
