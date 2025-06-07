package com.futbol.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futbol.demo.dto.CreateTeamDTO;
import com.futbol.demo.model.Team;
import com.futbol.demo.model.User;
import com.futbol.demo.repository.TeamRepository;

@Service
public class TeamService {

	@Autowired
	TeamRepository teamRepository;
	
	public List <Team> listTeam(){
		return teamRepository.findAll();
	}

	public Team saveTeam (Team team) {
		return teamRepository.save(team);
	}
	
	public void deleteTeam(long id) {
		teamRepository.deleteById(id);
	}
	
	public Optional <Team> getTeamById(Long id){
		return teamRepository.findById(id);
	}
	
	
	public void createTeam(CreateTeamDTO dto, User user) {
	    Team team = Team.builder()
	        .name(dto.getName())
	        .location(dto.getLocation())
	        .category(dto.getCategory())
	        .description(dto.getDescription())
	        .logoPath(dto.getLogoPath())
	        .user(user)
	        .build();

	    teamRepository.save(team);
	}
	
	public List<Team> findTeamsByUser(User user) {
	    return teamRepository.findByUser(user);
}
}
