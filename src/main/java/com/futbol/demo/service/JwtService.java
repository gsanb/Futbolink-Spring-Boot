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
	    
		// Extrae el nombre de usuario (subject) desde el token JWT.
	    public String extractUsername(String token) {
	        return Jwts.parser()
	                .verifyWith(getSignInKey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload()
	                .getSubject();
	    }
	    
		// Genera un token JWT para el usuario, incluyendo sus roles como claims personalizados.
	    public String generateToken(User user) {
	        Map<String, Object> claims = new HashMap<>();
	        // Esto debe coincidir con lo de UserDetailsService
	        claims.put("authorities", List.of("ROLE_" + user.getRole().toUpperCase()));
	        
	        return Jwts.builder()
	            .claims(claims)
	            .subject(user.getEmail())
	            .claim("id", user.getId()) 
	            .issuedAt(new Date())
	            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
	            .signWith(getSignInKey())
	            .compact();
	    }
	    
		// Genera un token de refresco (refresh token) sin claims adicionales.
	    public String generateRefreshToken(final User user) {
	        return buildToken(new HashMap<>(), user, refreshExpiration); // Sin claims extra
	    }
	
		// Construye un token JWT sin claims personalizados; versión simplificada para uso interno.
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
	
		
	    
		// Verifica si el token es válid, compara el email del usuario con el subject y verifica que no esté expirado
	    public boolean isTokenValid(String token, User user) {
	        final String username = extractUsername(token);
	        return (username.equals(user.getEmail())) && !isTokenExpired(token);
	    }
	    
		// Determina si un token ha expiradon comparando su fecha de expiración con la fecha actual
	    private boolean isTokenExpired(String token) {
	        return extractExpiration(token).before(new Date());
	    }
	    
		// Extrae la fecha de expiración desde el payload del token JWT.
	    private Date extractExpiration(String token) {
	        return Jwts.parser()
	                .verifyWith(getSignInKey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload()
	                .getExpiration();
	    }
	    
		// Genera y retorna la clave secreta para firmar/verificar los tokens JWT
	    private SecretKey getSignInKey() {
	        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }
	}
	
	
