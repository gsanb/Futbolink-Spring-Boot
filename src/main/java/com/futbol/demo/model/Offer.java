package com.futbol.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String positionNeeded;
    private int minExperience;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private User team; // El usuario con rol "TEAM" que crea la oferta
}
