package com.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.futbol.demo.model.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

	@Query(value = """
		    select t from Token t inner join User u 
		    on t.user.id = u.id 
		    where u.id = :id and (t.isExpired = false or t.isRevoked = false) 
		    """)
		List<Token> findAllValidTokenByUser(Long id);

  Optional<Token> findByToken(String token);
}
