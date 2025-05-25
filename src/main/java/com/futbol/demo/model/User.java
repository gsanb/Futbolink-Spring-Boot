package com.futbol.demo.model;

import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User {
	  @Id
	  @GeneratedValue
	  private Long id;

	  @Column(nullable = false)
	  private String name;

	  @Column(nullable = false, unique = true)
	  private String email;

	  @Column(nullable = false)
	  private String password;

	  @OneToMany(mappedBy = "user")
	  private List<Token> tokens;
	
	 // @Column(nullable = false)
	  private String role;		
}
