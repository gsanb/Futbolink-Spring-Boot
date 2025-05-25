package com.futbol.demo.controller;

public record AuthRequest(
        String email,
        String password
) {
}