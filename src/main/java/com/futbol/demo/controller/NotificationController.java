package com.futbol.demo.controller;

import com.futbol.demo.model.Notification;



import com.futbol.demo.model.User;
import com.futbol.demo.service.NotificationService;
import com.futbol.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

   
}