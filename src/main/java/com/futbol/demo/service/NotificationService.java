package com.futbol.demo.service;

import com.futbol.demo.model.*;
import com.futbol.demo.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate; 
    
	 //Crea una notificación para un jugador indicando si su solicitud para un equipo fue aceptada o rechazada,
	 //la guarda en la base de datos y la envía en tiempo real por WebSocket (he terminado usando un sistema de polling, a futuro me gustaria implementarlo en tiempo real con webSocket 100% funcional).
    public void createApplicationResponseNotification(Application application, boolean accepted) {
        String status = accepted ? "aceptada" : "rechazada";
        Notification notification = Notification.builder()
                .message(String.format("Tu aplicación al equipo %s ha sido %s", 
                        application.getTeam().getName(), status))
                .user(application.getPlayer().getUser())
                .type("APPLICATION_RESPONSE")
                .relatedEntityId(application.getId())
                .build();
        notificationRepository.save(notification);

        messagingTemplate.convertAndSend(
            "/topic/notifications/" + application.getPlayer().getUser().getId(), 
            notification
        );
    }
    
	 //Crea una notificación para el dueño del equipo cuando un jugador aplica a su equipo,
	 //la guarda en la base de datos y la envía en tiempo real de forma privada al usuario
    public void createTeamApplicationNotification(Team team, Player player) {
        Notification notification = Notification.builder()
                .message(String.format("%s ha aplicado a tu equipo %s", 
                        player.getUser().getName(), team.getName()))
                .user(team.getUser())
                .recipient(team.getUser().getEmail()) // ✅ Añade esto
                .type("TEAM_APPLICATION")
                .relatedEntityId(team.getId())
                .build();
        
        notificationRepository.save(notification);

        // Envía al destinatario específico
        messagingTemplate.convertAndSendToUser(
            team.getUser().getEmail(), // O username según tu auth
            "/queue/notifications",
            notification
        );
    }
    
    //Guarda una notificación personalizada en la base de datos.
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
    
    //Obtiene todas las notificaciones no leídas de un usuario, ordenadas por fecha de creación descendente.
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }
    
    // Marca como leídas todas las notificaciones de un usuario.
    public void markNotificationsAsRead(User user) {
        notificationRepository.markAllAsRead(user);
    }
}