package com.futbol.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.futbol.demo.model.Application;
import com.futbol.demo.model.ApplicationStatus;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByTeamId(Long teamId);
    List<Application> findByPlayerId(Long playerId);
    boolean existsByPlayerIdAndTeamId(Long playerId, Long teamId);
    
    Optional<Application> findFirstByPlayerIdAndTeamId(Long playerId, Long teamId);
    List<Application> findByPlayerUserIdAndStatus(Long userId, ApplicationStatus status);
    List<Application> findByTeamUserIdAndStatus(Long userId, ApplicationStatus status);


}
