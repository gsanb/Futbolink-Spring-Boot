package com.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.futbol.demo.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository <Player, Long> {
	
	
}
