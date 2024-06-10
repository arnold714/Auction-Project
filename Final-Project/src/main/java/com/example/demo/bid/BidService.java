package com.example.demo.bid;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.auction.Auction;

@Service
public class BidService {

	@Autowired
	private BidDao dao;
	
	
	public String save(BidDto dto) {
		Bid b=dao.save(Bid.create(dto));
		return b.toString();
	}
	public BidDto get(int num) {
		Bid b=dao.findById(num).orElse(null);
		if(b==null) {
			return null;
		}
		return BidDto.create(b);
	}
	public ArrayList<BidDto> getByParent(int parent){
		ArrayList<Bid> l=dao.findByParentOrderByPrice(new Auction(parent,null,0,0,null,"",null,null,null,"","",0));
		ArrayList<BidDto> list=new ArrayList<>();
		for(Bid b:l) {
			list.add(BidDto.create(b));
		}
		return list;
		
	}
}
