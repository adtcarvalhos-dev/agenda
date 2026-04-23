package com.igreja.agenda.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // injeta do application.properties
    @Value("${jwt.secret}")
    private String SECRET;

    // gera a chave com base na secret
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(Usuario usuario) {
    return Jwts.builder()
            .subject(usuario.getEmail())
            .claim("role", usuario.getRole().name()) // 🔥 ESSENCIAL
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(getSigningKey())
            .compact();
}

    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
