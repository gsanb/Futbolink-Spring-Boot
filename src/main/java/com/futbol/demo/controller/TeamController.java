package com.futbol.demo.controller;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.futbol.demo.model.Team;
import com.futbol.demo.model.User;
import com.futbol.demo.repository.UserRepository;
import com.futbol.demo.service.TeamService;



@RestController
@RequestMapping("/api/teams")
public class TeamController {
	
	@Autowired
	private TeamService teamService;
	@Autowired
	private UserRepository userRepository;
	
	
	 @GetMapping
	    public ResponseEntity<List<Team>> getAllTeams() {
	        return ResponseEntity.ok(teamService.listTeam());
	    }
	 
	 // Endpoint para equipos del usuario actual
	    @GetMapping("/my-teams")
	    @PreAuthorize("hasRole('TEAM')")
	    public ResponseEntity<List<Team>> getUserTeams(Authentication authentication) {
	        String email = authentication.getName();
	        User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
	        
	        return ResponseEntity.ok(teamService.findTeamsByUser(user));
	    }
	    //Crear equipo
	 @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	 public ResponseEntity<?> createTeam(
	     @RequestParam("name") String name,
	     @RequestParam("location") String location,
	     @RequestParam("category") String category,
	     @RequestParam("description") String description,
	     @RequestParam(value = "logo", required = false) MultipartFile logoFile,
	     Authentication authentication
	 ) {
	     String email = authentication.getName();
	     System.out.println("EMAIL AUTENTICADO: " + email);

	     User user = userRepository.findByEmail(email)
	         .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

	     String logoPath = null;
	     if (logoFile != null && !logoFile.isEmpty()) {
	         try {
	             String filename = UUID.randomUUID() + "_" + logoFile.getOriginalFilename();
	             Path path = Paths.get("logos/" + filename);
	             Files.createDirectories(path.getParent());
	             Files.write(path, logoFile.getBytes());
	             logoPath = "/logos/" + filename;
	         } catch (IOException e) {
	             return ResponseEntity.status(500).body("Error al subir imagen");
	         }
	     }

	     Team team = Team.builder()
	         .name(name)
	         .location(location)
	         .category(category)
	         .description(description)
	         .logoPath(logoPath)
	         .user(user)
	         .build();

	     Team savedTeam = teamService.saveTeam(team);
	     return ResponseEntity.status(201).body(savedTeam);
	 }
	 
	 //Obtener por iD
	 @GetMapping("/{id}")
	 public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
	     Team team = teamService.getTeamById(id)
	         .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
	     return ResponseEntity.ok(team);
	 }
	 
	 	//Para editar el equipo
	    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	    @PreAuthorize("hasRole('TEAM')")
	    public ResponseEntity<Team> updateTeam(
	        @PathVariable Long id,
	        @RequestParam(value = "name", required = false) String name,
	        @RequestParam(value = "location", required = false) String location,
	        @RequestParam(value = "category", required = false) String category,
	        @RequestParam(value = "description", required = false) String description,
	        @RequestParam(value = "logo", required = false) MultipartFile logoFile
	    ) {
	        Team existingTeam = teamService.getTeamById(id)
	            .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

	        // Actualizar solo los campos proporcionados
	        if (name != null) existingTeam.setName(name);
	        if (location != null) existingTeam.setLocation(location);
	        if (category != null) existingTeam.setCategory(category);
	        if (description != null) existingTeam.setDescription(description);
	        
	        if (logoFile != null && !logoFile.isEmpty()) {
	            String newLogoPath = handleFileUpload(logoFile);
	            // Eliminar la imagen anterior si existe
	            if (existingTeam.getLogoPath() != null) {
	                deleteFile(existingTeam.getLogoPath());
	            }
	            existingTeam.setLogoPath(newLogoPath);
	        }

	        Team updatedTeam = teamService.saveTeam(existingTeam);
	        return ResponseEntity.ok(updatedTeam);
	    }
	    
	    //Eliminar equipo
	    @DeleteMapping("/{id}")
	    @PreAuthorize("hasRole('TEAM')")
	    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
	        Team team = teamService.getTeamById(id)
	            .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
	        
	        // Eliminar la imagen del logo si existe
	        if (team.getLogoPath() != null) {
	            deleteFile(team.getLogoPath());
	        }
	        
	        teamService.deleteTeam(id);
	        return ResponseEntity.noContent().build();
	    }

	    // Métodos auxiliares
	    private String handleFileUpload(MultipartFile file) {
	        if (file == null || file.isEmpty()) {
	            return null;
	        }
	        
	        try {
	            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
	            Path path = Paths.get("logos/" + filename);
	            Files.createDirectories(path.getParent());
	            Files.write(path, file.getBytes());
	            return "/logos/" + filename;
	        } catch (IOException e) {
	            throw new RuntimeException("Error al subir imagen", e);
	        }
	    }

	    private void deleteFile(String filePath) {
	        if (filePath == null) return;
	        
	        try {
	            Path path = Paths.get(filePath.substring(1)); // Elimina la / inicial
	            Files.deleteIfExists(path);
	        } catch (IOException e) {
	            System.err.println("Error al eliminar el archivo: " + filePath);
	        }
	    }
	}

