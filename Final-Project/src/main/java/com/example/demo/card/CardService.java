package com.example.demo.card;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {
	
	@Autowired
	private CardDao dao;
	
	public CardDto saveCard(CardDto dto) {
		Card c = dao.save(new Card(dto.getCardnum(), dto.getExpiryDate(),dto.getCvc(),dto.getPwd(),dto.getPrice()));
		return new CardDto(c.getCardnum(), c.getExpiryDate(),c.getCvc(),c.getPwd(),c.getPrice());
	}
	
	
	public CardDto getCard(int cardnum) {
		Card c = dao.findById(cardnum).orElse(null);
		if (c == null) {
			return null;
		}
		return new CardDto(c.getCardnum(),c.getExpiryDate(),c.getCvc(), c.getPwd(), c.getPrice());
		
	}
	
	
	public void deleteCard(int cardnum) {
		dao.deleteById(cardnum);
	}
	
	public List<CardDto> getAllCards(){
		List<Card> cards = dao.findAll();
		return cards.stream().map(card -> new CardDto(card.getCardnum(),card.getExpiryDate(),card.getCvc(),card.getPwd(),card.getPrice())).collect(Collectors.toList());
	}
}
