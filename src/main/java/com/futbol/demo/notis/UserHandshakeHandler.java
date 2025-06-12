package com.futbol.demo.notis;

import com.futbol.demo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Autowired
    private JwtService jwtService;
    
    // Determinamos el usuario autenticado a partir del token JWT extraído durante el handshake
    @Override
    protected Principal determineUser(org.springframework.http.server.ServerHttpRequest request,
                                      org.springframework.web.socket.WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        String token = (String) attributes.get("token");
        if (token != null) {
            String username = jwtService.extractUsername(token);
            return new StompPrincipal(username);
        }
        return null;
    }
}
