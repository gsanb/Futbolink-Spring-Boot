package com.futbol.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futbol.demo.dto.CreateTeamDTO;
import com.futbol.demo.model.Team;
import com.futbol.demo.model.User;
import com.futbol.demo.repository.UserRepository;
import com.futbol.demo.service.TeamService;
import com.futbol.demo.service.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

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

	@PostMapping
	@PreAuthorize("hasRole('TEAM')")
	public ResponseEntity<?> createTeam(@RequestBody CreateTeamDTO dto) {
	    // Obtener el email del usuario autenticado
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String email = authentication.getName();

	    // Buscar al usuario en la base de datos
	    User user = userRepository.findByEmail(email)
	        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

	    teamService.createTeam(dto, user);

	    return ResponseEntity.ok("Equipo creado correctamente");
	}

}
