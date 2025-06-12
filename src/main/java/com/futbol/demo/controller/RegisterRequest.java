package com.futbol.demo.controller;

public record RegisterRequest(
		//Datos requeridos para realizar el registro
        String name,
        String email,
        String password,
        String role
) {
}