package com.futbol.demo.config;

import com.futbol.demo.model.User;

import com.futbol.demo.notis.JwtAuthHandshakeInterceptor;
import com.futbol.demo.notis.UserHandshakeHandler;
import com.futbol.demo.service.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import java.util.List;
import java.util.Map;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	private final UserHandshakeHandler userHandshakeHandler;
    private final JwtAuthHandshakeInterceptor jwtAuthHandshakeInterceptor;
    private final JwtService jwtService; // Solo si lo usas en otros métodos

    // Configura el broker de mensajes STOMP (rutas internas y destinos de usuario)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue", "/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
    
    // Registra el endpoint WebSocket accesible desde el cliente
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:*") //  Usa el mismo origen que CORS
                .setHandshakeHandler(userHandshakeHandler)
                .addInterceptors(jwtAuthHandshakeInterceptor)
                .withSockJS();
    }
    
    // Configura CORS para permitir que el frontend acceda a este backend
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
    
   

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                    String token = (String) sessionAttributes.get("token");
                    User user = (User) sessionAttributes.get("user");

                    if (token != null && user != null) {
                        if (jwtService.isTokenValid(token, user)) {
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(user, null, List.of());
                            accessor.setUser(auth);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        } else {
                            System.out.println("Token inválido en WebSocket");
                            return null;
                        }
                    } else {
                        System.out.println("Token o usuario no encontrados en WebSocket");
                        return null;
                    }
                }

                return message;
            }
        });
    }

}
