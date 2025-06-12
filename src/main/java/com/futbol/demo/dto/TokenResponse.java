package com.futbol.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
	
		// Representa una respuesta que contiene el token de acceso y el token de actualización en formato JSON
		
		// Almacenamos el JWT de acceso y el de actualziación
        @JsonProperty("access_token") 
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken
) {
}