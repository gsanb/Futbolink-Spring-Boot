package com.futbol.demo.controller;


import com.futbol.demo.dto.PasswordChangeRequest;
import com.futbol.demo.dto.UserUpdateRequest;
import com.futbol.demo.model.User;
import com.futbol.demo.model.UserResponse;
import com.futbol.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Cambiar contraseña
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Contraseña cambiada");
    }

    // Eliminar cuenta
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        userRepository.delete(user);
        return ResponseEntity.ok("Cuenta eliminada");
    }

    // Actualizar perfil
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody UserUpdateRequest request, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAvatarPath(request.getAvatarPath());

        userRepository.save(user);
        return ResponseEntity.ok("Perfil actualizado");
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
