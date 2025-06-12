package com.futbol.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.futbol.demo.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByApplicationIdOrderByTimestampAsc(Long applicationId);
    
    Optional<ChatMessage> findTopByApplicationIdOrderByTimestampDesc(Long applicationId);

}
