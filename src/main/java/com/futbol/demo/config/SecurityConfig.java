package com.futbol.demo.config;




import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.futbol.demo.model.Token;
import com.futbol.demo.repository.TokenRepository;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final TokenRepository tokenRepository;
//requestMatchers("/api/player/profile").hasRole("PLAYER")
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable).cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(req ->
                req
                .requestMatchers("/", "/index.html", "/assets/**", "/vite.svg", "/favicon.ico").permitAll()
                .requestMatchers("/auth/**", "/logos/**", "/avatars/**").permitAll()           
                .requestMatchers(HttpMethod.POST, "/api/users/me").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/users/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/users/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").authenticated()
                
                
                // Endpoints públicos
                .requestMatchers(HttpMethod.GET, "/api/teams").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/teams/{id}").permitAll() // Permitir ver detalles sin autenticación
                
                // Endpoints de chat
                .requestMatchers(HttpMethod.GET, "/api/chat/**").hasAnyRole("PLAYER", "TEAM")
                .requestMatchers(HttpMethod.POST, "/api/chat/**").hasAnyRole("PLAYER", "TEAM")
                
                // Endpoints para TEAM
                .requestMatchers(HttpMethod.GET, "/api/teams/my-teams").hasRole("TEAM")
                .requestMatchers(HttpMethod.POST, "/api/teams").hasRole("TEAM")
                .requestMatchers(HttpMethod.PUT, "/api/teams/**").hasRole("TEAM")
                .requestMatchers(HttpMethod.DELETE, "/api/teams/**").hasRole("TEAM")
                
                .requestMatchers(HttpMethod.GET, "/api/notifications").authenticated() // Solo usuarios autenticados
                .requestMatchers(HttpMethod.POST, "/api/notifications/mark-as-read").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/applications/status-by-id/**").hasRole("PLAYER")

                // Endpoints para aplicaciones
                .requestMatchers(HttpMethod.GET, "/api/applications/team").hasRole("TEAM")
                .requestMatchers(HttpMethod.GET, "/api/player/{id}").hasAnyRole("PLAYER", "TEAM")
                .requestMatchers(HttpMethod.GET, "/api/applications/status/{teamId}").hasAnyRole("PLAYER", "TEAM")
                    .anyRequest().authenticated()
            )

                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/auth/logout")
                                .addLogoutHandler(this::logout)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
        ;

        return http.build();
    }

    private void logout(
            final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication
    ) {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String jwt = authHeader.substring(7);
        final Token storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setIsExpired(true);
            storedToken.setIsRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
    
 // Configuración CORS para Spring Security
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true); // Necesario para cookies/tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}