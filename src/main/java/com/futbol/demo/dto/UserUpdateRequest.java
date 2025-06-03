package com.futbol.demo.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String name;
    private String email;
    private String avatarPath; // ruta de imagen opcional
}