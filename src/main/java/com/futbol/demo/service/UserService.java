package com.futbol.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.futbol.demo.model.User;
import com.futbol.demo.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public List<User> listUser(){
		return userRepository.findAll();
	}
	
	public User saveUser(User user) {
		return userRepository.save(user);
	}
	
	public void deleteUser(Long id) {
		userRepository.deleteById(id);	
	}
	
	public Optional<User> findById(Long id){
		return userRepository.findById(id);
	}
	
	public User register(User user) {
	     user.setPassword(passwordEncoder.encode(user.getPassword()));
	   	 return userRepository.save(user);
	}
}