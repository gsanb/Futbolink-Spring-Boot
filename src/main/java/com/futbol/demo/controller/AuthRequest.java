package com.futbol.demo.controller;

public record AuthRequest(
		//Lo necesario para realizar el login
        String email,
        String password
) {
}