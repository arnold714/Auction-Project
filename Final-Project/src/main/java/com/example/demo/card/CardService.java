package com.example.demo.card;

import java.util.ArrayList;
import java.util.List;

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
	
	public ArrayList<CardDto> getByName(String holdername) {
		List<Card> l = dao.findByHoldername(holdername);
		ArrayList<CardDto> list = new ArrayList<>();
		for (Card c: l) {
			list.add(new CardDto(c.getCardnum(),c.getExpiryDate(),c.getCvc(),c.getPwd(),c.getPrice()));
		}
		return list;
		
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
}
