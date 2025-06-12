package com.futbol.demo.controller;

import com.futbol.demo.model.Notification;


import com.futbol.demo.model.User;
import com.futbol.demo.service.NotificationService;
import com.futbol.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    //Obtener las notioficaciones
    @GetMapping
    public ResponseEntity<?> getUnreadNotifications() {
        User user = userService.getCurrentUser();
        List<Notification> notifications = notificationService.getUnreadNotifications(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("notifications", notifications != null ? notifications : Collections.emptyList());
        
        return ResponseEntity.ok(response);
    }

    //Marcar como leidas
    @PostMapping("/mark-as-read")
    public ResponseEntity<?> markAsRead() {
        User user = userService.getCurrentUser();
        notificationService.markNotificationsAsRead(user);
        return ResponseEntity.ok().build();
    }

    //Enviarlas 
    @MessageMapping("/send-notification")
    public void sendNotification(@Payload Notification notification, Principal principal) {
        System.out.println("Enviando notificación a: " + principal.getName());
        
        notification.setRecipient(principal.getName());
        notification.setCreatedAt(LocalDateTime.now());
        
        messagingTemplate.convertAndSendToUser(
            principal.getName(),
            "/queue/notifications",
            notification
        );
    }
    
    @MessageMapping("/notifications")
    public void handleNotificationSubscription(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Notification> unread = notificationService.getUnreadNotifications(user);
        messagingTemplate.convertAndSendToUser(
            principal.getName(),
            "/queue/notifications",
            unread
        );
    }
}