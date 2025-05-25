package com.futbol.demo.dto;



public record UserDto(
        Long id,
        String name,
        String email,
        String role
) {}