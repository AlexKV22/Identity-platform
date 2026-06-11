package org.todo.accountservice.infrastructure.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.todo.accountservice.infrastructure.security.jwt.exception.InvalidJwtException;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;

@Component
public class JWTTokenProvider {
    private final SecretKey key;

    @Value("${jwt.expiration}")
    private long tokenValidityInMilliseconds;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshTokenValidityInMilliseconds;

    public JWTTokenProvider (@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateJWTToken(String email) {
        Instant now = Instant.now();
        Instant expire = now.plusMillis(tokenValidityInMilliseconds);
        return Jwts.builder().subject(email).issuedAt(Date.from(now)).expiration(Date.from(expire)).signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(String email) {
        Instant now = Instant.now();
        Instant expire = now.plusMillis(refreshTokenValidityInMilliseconds);
        return Jwts.builder().subject(email).issuedAt(Date.from(now)).expiration(Date.from(expire)).signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException();
        }

    }
}
