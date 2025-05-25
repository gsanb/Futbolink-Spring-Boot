package com.futbol.demo.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name="offers")
public class Offer {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String description;
	private String position;
	@Builder.Default
	private LocalDateTime created_at = LocalDateTime.now();
	
	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OfferStatus status = OfferStatus.ACTIVE;

	
	@OneToOne
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	private Team team;

	
}

