package com.futbol.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futbol.demo.model.Offer;
import com.futbol.demo.repository.OfferRepository;

@Service
public class OfferService {
	
	@Autowired
	OfferRepository offerRepository;
	
	public List <Offer>listOffer(){
		return offerRepository.findAll();
	}
	
	public Offer saveOffer(Offer offer) {
		return offerRepository.save(offer);	
	}
	
	public void deleteOffer(Long id) {
		offerRepository.deleteById(id);	
	}
	
	public Optional <Offer> findById(Long id) {
		return offerRepository.findById(id);
	}
}
