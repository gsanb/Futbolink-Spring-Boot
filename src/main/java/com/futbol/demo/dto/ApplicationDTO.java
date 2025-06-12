package com.futbol.demo.dto;


import com.futbol.demo.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicationDTO {
    private Long id;
    private Long playerId;
    private String playerUsername;
    private String message;
    private ApplicationStatus status;
    private String teamName;
}
