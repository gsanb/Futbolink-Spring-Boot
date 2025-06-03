package com.futbol.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futbol.demo.model.Player;
import com.futbol.demo.model.User;
import com.futbol.demo.repository.PlayerRepository;
import com.futbol.demo.repository.UserRepository;

import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerController {

	@Autowired
    private final PlayerRepository playerRepo;
    @Autowired
    private final UserRepository userRepo;

    @PostMapping("/profile")
    public ResponseEntity<?> saveProfile(@RequestBody Player data, Authentication auth) {
        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!user.getRole().equals("PLAYER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        Player player = playerRepo.findByUser(user).orElse(new Player());
        player.setUser(user);
        player.setAge(data.getAge());
        player.setPosition(data.getPosition());
        player.setSkills(data.getSkills());
        player.setExperience(data.getExperience());
        player.setDescription(data.getDescription());

        return ResponseEntity.ok(playerRepo.save(player));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(Authentication auth) {
        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return playerRepo.findByUser(user)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable Long id) {
        return playerRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)  // Nota el operador diamante explícito
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jugador no encontrado"));
    }
}
