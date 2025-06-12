package com.futbol.demo.controller;

import com.futbol.demo.dto.ChatSummaryDTO;
import com.futbol.demo.model.ChatMessage;
import com.futbol.demo.service.ChatService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
	
	//Controller de chat, obtener mensajes, enviar y obtener los usuarios
	
    private final ChatService chatService;

    @GetMapping("/{applicationId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long applicationId) {
        return ResponseEntity.ok(chatService.getMessages(applicationId));
    }

    @PostMapping("/{applicationId}")
    public ResponseEntity<ChatMessage> sendMessage(
        @PathVariable Long applicationId,
        @RequestBody Map<String, String> body
    ) {
        String content = body.get("content");
        return ResponseEntity.ok(chatService.sendMessage(applicationId, content));
    }
    
    @GetMapping
    public ResponseEntity<List<ChatSummaryDTO>> getUserChats() {
        return ResponseEntity.ok(chatService.listUserChats());
    }

    
}
