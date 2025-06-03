package com.futbol.demo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.futbol.demo.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

   /* public String generateToken(final User user) {
        return buildToken(user, jwtExpiration);
    }*/
   /* public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole()); // ✅ Añadir rol al token
        return buildToken(claims, user, jwtExpiration);
    }
*/
    
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        // Esto debe coincidir con lo de UserDetailsService
        claims.put("authorities", List.of("ROLE_" + user.getRole().toUpperCase()));
        
        return Jwts.builder()
            .claims(claims)
            .subject(user.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSignInKey())
            .compact();
    }
    public String generateRefreshToken(final User user) {
        return buildToken(new HashMap<>(), user, refreshExpiration); // Sin claims extra
    }

    // ✅ Método sobrecargado con claims
    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    // 🧼 Mantén este si quieres para uso interno sin claims personalizados
    private String buildToken(User user, long expiration) {
        return buildToken(new HashMap<>(), user, expiration);
    }

    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    private SecretKey getSignInKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}