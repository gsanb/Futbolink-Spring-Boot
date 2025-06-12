package com.futbol.demo.repository;



import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.futbol.demo.model.Team;
import com.futbol.demo.model.User;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
	List<Team> findByUserId(Long user);
	
	 
	 List<Team> findByUser(User user);

}

