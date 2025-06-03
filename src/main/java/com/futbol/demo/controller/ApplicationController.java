package com.futbol.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futbol.demo.model.Application;
import com.futbol.demo.model.ApplicationStatus;
import com.futbol.demo.model.Player;
import com.futbol.demo.model.Team;
import com.futbol.demo.model.User;
import com.futbol.demo.repository.ApplicationRepository;
import com.futbol.demo.repository.PlayerRepository;
import com.futbol.demo.repository.TeamRepository;
import com.futbol.demo.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final UserService userService; // Tu servicio que devuelve el usuario actual

    @PostMapping("/apply/{teamId}")
    public ResponseEntity<?> applyToTeam(@PathVariable Long teamId, @RequestBody String message) {
        User currentUser = userService.getCurrentUser();
        Player player = playerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        if (applicationRepository.existsByPlayerIdAndTeamId(player.getId(), team.getId())) {
            return ResponseEntity.badRequest().body("Ya has aplicado a este equipo");
        }

        Application app = Application.builder()
                .player(player)
                .team(team)
                .message(message)
                .build();
        applicationRepository.save(app);
        return ResponseEntity.ok("Aplicación enviada");
    }

    @GetMapping("/team")
    public List<Application> getTeamApplications() {
        User currentUser = userService.getCurrentUser();
        Team team = teamRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        return applicationRepository.findByTeamId(team.getId());
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptApplication(@PathVariable Long id) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aplicación no encontrada"));
        app.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepository.save(app);
        return ResponseEntity.ok("Aplicación aceptada");
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable Long id) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aplicación no encontrada"));
        app.setStatus(ApplicationStatus.REJECTED);
        applicationRepository.save(app);
        return ResponseEntity.ok("Aplicación rechazada");
    }
    
    @GetMapping("/status/{teamId}")
    public ResponseEntity<?> getApplicationStatus(@PathVariable Long teamId) {
        User currentUser = userService.getCurrentUser();
        Player player = playerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        Application app = applicationRepository.findByPlayerIdAndTeamId(player.getId(), teamId)
                .orElseThrow(() -> new RuntimeException("No se encontró una postulación a este equipo"));

        return ResponseEntity.ok(app.getStatus());
    }
    
    

}

