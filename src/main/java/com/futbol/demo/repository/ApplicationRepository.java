package com.futbol.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.futbol.demo.model.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByTeamId(Long teamId);
    List<Application> findByPlayerId(Long playerId);
    boolean existsByPlayerIdAndTeamId(Long playerId, Long teamId);
    Optional<Application> findByPlayerIdAndTeamId(Long playerId, Long teamId);

}
