package com.futbol.demo.controller;

public record RegisterRequest(
        String name,
        String email,
        String password
) {
}