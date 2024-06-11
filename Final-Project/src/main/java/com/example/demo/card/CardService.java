package com.example.demo.card;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {
	
	@Autowired
	private CardDao dao;
	
	//카드 등록
	public void save(CardDto dto) {
		dao.save(new Card(dto.getCardnum(),dto.getExpiryDate(),dto.getCvc(),dto.getPwd(),dto.getPrice()));
		
	}
	
	//카드 번호로 검색
	public CardDto getCard(int cardnum) {
		Card c = dao.findById(cardnum).orElse(null);
		if (c == null) {
			return null;
		}
		return new CardDto(c.getCardnum(),c.getExpiryDate(),c.getCvc(), c.getPwd(), c.getPrice());
	}
	
	//전체 카드 목록
	public ArrayList<CardDto> getAllCards(){
		List<Card> l = dao.findAll();
		ArrayList<CardDto> list = new ArrayList<CardDto>();
		for(Card c:l) {
			list.add(new CardDto(c.getCardnum(),c.getExpiryDate(),c.getCvc(),c.getPwd(),c.getPrice()));
		}
		return list;
	}
	
	//삭제
	public void delCard(int cardnum) {
		dao.deleteById(cardnum);
	}
	
	
}
