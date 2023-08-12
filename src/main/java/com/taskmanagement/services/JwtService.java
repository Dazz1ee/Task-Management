package com.taskmanagement.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpiration;

    @Value("${jwt.accessExpiration}")
    private long createExpiration;
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return null;
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extreactClaims(token));
    }

    private Claims extreactClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    public String generateToken(Map<String, Object> map, UserDetails userDetails) {
        return buildToken(map, userDetails, createExpiration);
    }

    public String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            return userDetails.getUsername().equals(extractUsername(token)) && !isTokenExpired(token);

        } catch (ExpiredJwtException exception) {
            log.error(String.format("token [%s] by %s expired", token, userDetails.getUsername()));
            return false;
        }
    }
    public boolean isTokenExpired(String token) {
        try {
            return System.currentTimeMillis() >= extractClaim(token, Claims::getExpiration).getTime();
        }catch (ExpiredJwtException e){
            log.error(String.format("token [%s]  expired", token));
            return false;
        }
    }

    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }
}
