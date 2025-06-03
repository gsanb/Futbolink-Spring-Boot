package com.futbol.demo.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.futbol.demo.model.Player;
import com.futbol.demo.model.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
	Optional<Team> findByUserId(Long user);

}

