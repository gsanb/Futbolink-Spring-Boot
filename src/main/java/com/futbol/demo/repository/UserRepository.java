package com.futbol.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.futbol.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {

	Optional<User> findByEmail(String email);

}
