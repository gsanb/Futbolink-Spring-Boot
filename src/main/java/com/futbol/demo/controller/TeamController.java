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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	     teamService.saveTeam(team);
	     return ResponseEntity.ok("Equipo creado correctamente");
	 }

	 @GetMapping("/{id}")
	 public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
	     Team team = teamService.getTeamById(id)
	         .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
	     return ResponseEntity.ok(team);
	 }
	 
	 @GetMapping("/test")
	 public String test(Authentication authentication) {
	     System.out.println("Authorities: " + authentication.getAuthorities());
	     return "Test successful";
	 }
/*
	 @PostMapping
		public ResponseEntity<?> createTeam(@RequestBody CreateTeamDTO dto) {
			System.out.println(">>> Llegó petición a /api/teams"); // Log simple
		    // Obtener el email del usuario autenticado
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    String email = authentication.getName();

		    // Buscar al usuario en la base de datos
		    User user = userRepository.findByEmail(email)
		        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

		    teamService.createTeam(dto, user);

		    return ResponseEntity.ok("Equipo creado correctamente");
		}
		*/
	 
/*
	@PostMapping(value = "/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadLogo(@RequestParam("file") MultipartFile file) {
	    try {
	        String folder = "logos/";
	        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
	        Path path = Paths.get(folder + filename);

	        Files.createDirectories(path.getParent());
	        Files.write(path, file.getBytes());

	        // Devolver la ruta que se guardará en la BD
	        return ResponseEntity.ok("/logos/" + filename);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("Error al guardar la imagen");
	    }
	}*/
	
}
