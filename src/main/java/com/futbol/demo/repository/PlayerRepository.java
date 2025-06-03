package com.futbol.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.futbol.demo.model.Player;
import com.futbol.demo.model.User;

@Repository
public interface PlayerRepository extends JpaRepository <Player, Long> {
	Optional<Player> findByUserId(Long user);
	Optional<Player> findByUser(User user);
	
}
