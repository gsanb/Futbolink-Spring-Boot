package com.futbol.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatSummaryDTO {
    private Long applicationId;
    private String teamName;
    private String playerName;
    private String lastMessage;
    private String lastTimestamp;
}
