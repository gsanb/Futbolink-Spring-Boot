package com.futbol.demo.notis;

import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtAuthHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                  WebSocketHandler wsHandler, Map<String, Object> attributes) {
        
        if (!(request instanceof ServletServerHttpRequest)) return false;

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        
        // Intenta obtener el token del header
        String token = servletRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            token = servletRequest.getParameter("token");
        }

        // Validamos parámetros esenciales
        String userId = servletRequest.getParameter("userId");
        if (token == null || userId == null) {
            response.setStatusCode(HttpStatus.FORBIDDEN); // ⚠️ Explicitamente rechaza
            return false;
        }

        // Guarda en atributos para el handshake
        attributes.put("token", token);
        attributes.put("userId", userId);
        return true;
    }

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// TODO Auto-generated method stub
		
	}
}