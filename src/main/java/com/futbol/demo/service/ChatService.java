package com.futbol.demo.service;

import java.time.LocalDateTime;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.futbol.demo.dto.ChatSummaryDTO;
import com.futbol.demo.model.*;
import com.futbol.demo.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatRepo;
    private final ApplicationRepository applicationRepo;
    private final UserService userService;
    private final PlayerRepository playerRepo;
    private final TeamRepository teamRepo;
    private final NotificationService notificationService;
    
	  //Retorna la lista de mensajes asociados a una aplicación, ordenados por fecha ascendente,
	  //solo si el usuario actual tiene permiso para acceder.
    public List<ChatMessage> getMessages(Long applicationId) {
        Application app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Aplicación no encontrada"));

        validateAccess(app);
        return chatRepo.findByApplicationIdOrderByTimestampAsc(applicationId);
    }

	//Envía un mensaje en el contexto de una aplicación aceptada, asignando el rol del emisor,
	//creando una notificación para el receptor, y enviándola vía WebSocket.
    public ChatMessage sendMessage(Long applicationId, String content) {
        Application app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Aplicación no encontrada"));

        if (app.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new RuntimeException("No puedes chatear en una aplicación no aceptada");
        }

        String senderRole = getSenderRole(app);

        ChatMessage message = ChatMessage.builder()
                .application(app)
                .senderRole(senderRole)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        ChatMessage savedMessage = chatRepo.save(message);

        // CREAR NOTIFICACIÓN
        User recipientUser;
        String senderName;

        if (senderRole.equals("PLAYER")) {
            recipientUser = app.getTeam().getUser();
            senderName = app.getPlayer().getName();
        } else {
            recipientUser = app.getPlayer().getUser();
            senderName = app.getTeam().getName();
        }

        Notification notification = Notification.builder()
                .message(senderName + " te ha enviado un mensaje")
                .user(recipientUser)
                .type("CHAT_MESSAGE")
                .relatedEntityId(app.getId())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationService.saveNotification(notification);
        return savedMessage;
    }
    
    //Verifica si el usuario actual tiene acceso a una aplicación, ya sea como jugador o como dueño del equipo.
    private void validateAccess(Application app) {
        User currentUser = userService.getCurrentUser();
        boolean isPlayer = playerRepo.findByUserId(currentUser.getId())
                .map(p -> p.getId().equals(app.getPlayer().getId()))
                .orElse(false);

        boolean isTeam = teamRepo.findByUserId(currentUser.getId()).stream()
                .anyMatch(t -> t.getId().equals(app.getTeam().getId()));

        if (!isPlayer && !isTeam) {
            throw new RuntimeException("No tienes permiso para ver este chat");
        }
    }
    
    //Determina el rol del emisor del mensaje (PLAYER o TEAM) basado en el usuario autenticado.
    private String getSenderRole(Application app) {
        User currentUser = userService.getCurrentUser();
        if (app.getPlayer().getUser().getId().equals(currentUser.getId())) {
            return "PLAYER";
        }
        if (app.getTeam().getUser().getId().equals(currentUser.getId())) {
            return "TEAM";
        }
        throw new RuntimeException("No tienes permiso para enviar mensajes en este chat");
    }
    
	//Retorna un resumen de los chats activos del usuario autenticado, ya sea como jugador o como dueño de un equipo.
	//Incluye el último mensaje de cada aplicación aceptada.
    public List<ChatSummaryDTO> listUserChats() {
        User currentUser = userService.getCurrentUser();
        
        boolean isPlayer = playerRepo.findByUserId(currentUser.getId()).isPresent();
        boolean isTeam = !teamRepo.findByUserId(currentUser.getId()).isEmpty();

        List<Application> applications;

        if (isPlayer) {
            applications = applicationRepo.findByPlayerUserIdAndStatus(currentUser.getId(), ApplicationStatus.ACCEPTED);
        } else if (isTeam) {
            applications = applicationRepo.findByTeamUserIdAndStatus(currentUser.getId(), ApplicationStatus.ACCEPTED);
        } else {
            throw new RuntimeException("No tienes chats disponibles");
        }

        return applications.stream().map(app -> {
            ChatMessage lastMessage = chatRepo.findTopByApplicationIdOrderByTimestampDesc(app.getId()).orElse(null);
            return new ChatSummaryDTO(
                app.getId(),
                app.getTeam().getName(),
                app.getPlayer().getName(),
                lastMessage != null ? lastMessage.getContent() : "Sin mensajes",
                lastMessage != null ? lastMessage.getTimestamp().toString() : ""
            );
        }).toList();
    }
}


