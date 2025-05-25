package com.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.futbol.demo.model.Offer;

@Repository
public interface OfferRepository extends JpaRepository <Offer, Long> {

}
