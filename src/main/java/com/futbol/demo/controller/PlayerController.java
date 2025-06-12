package com.futbol.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    
    //Crear jugador
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveProfile(
        @RequestParam("name") String name,
        @RequestParam("age") int age,
        @RequestParam("position") String position,
        @RequestParam("skills") String skills,
        @RequestParam("experience") int experience,
        @RequestParam("description") String description,
        @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
        Authentication auth
    ) {
        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!user.getRole().equals("PLAYER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        Player player = playerRepo.findByUser(user).orElse(new Player());
        player.setUser(user);
        player.setName(name);
        player.setAge(age);
        player.setPosition(position);
        player.setSkills(skills);
        player.setExperience(experience);
        player.setDescription(description);

        // Guardar avatar si se envía
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String filename = UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
                Path path = Paths.get("avatars/" + filename);
                Files.createDirectories(path.getParent());
                Files.write(path, avatarFile.getBytes());
                player.setPhotoUrl("/avatars/" + filename);
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Error al subir avatar");
            }
        }

        return ResponseEntity.ok(playerRepo.save(player));
    }

    //Obtener perfil
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
    
    // Subir avatar
    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String folder = "avatars/";
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(folder + filename);

            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            return ResponseEntity.ok("/avatars/" + filename);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al guardar avatar");
        }
    }
    
    
}
