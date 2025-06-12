package com.futbol.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;
    
    @Column(nullable = false)
    private String recipient;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String type; // "TEAM_APPLICATION", "APPLICATION_RESPONSE"

    @Column
    private Long relatedEntityId; // ID de la aplicación o equipo relacionado
    
    @PrePersist
    protected void onCreate() {
        if (this.recipient == null && this.user != null) {
            this.recipient = this.user.getEmail(); 
        }
    }
}
