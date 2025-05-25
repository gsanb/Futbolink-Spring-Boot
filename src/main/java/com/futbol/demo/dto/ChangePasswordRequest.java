package com.futbol.demo.dto;


public record ChangePasswordRequest(
    String currentPassword,
    String newPassword
) {}