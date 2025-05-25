package com.futbol.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futbol.demo.model.Player;
import com.futbol.demo.repository.PlayerRepository;

@Service
public class PlayerService {

	@Autowired
	PlayerRepository playerRepository;	
	
	public List <Player> listPlayer(){
		return playerRepository.findAll();		
	}
	
	public Player savePlayer(Player player) {
		return playerRepository.save(player);
	}
	
	public void deletePlayer(Long id) {
		playerRepository.deleteById(id);
	}
	
	public Optional <Player> findById(Long id){
		return playerRepository.findById(id);
	}

}