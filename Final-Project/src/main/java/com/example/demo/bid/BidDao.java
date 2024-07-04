package com.example.demo.bid;

import java.util.ArrayList;

import com.example.demo.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.auction.Auction;

@Repository
public interface BidDao extends JpaRepository<Bid, Integer> {

	ArrayList<Bid>findByParentOrderByPrice(Auction parent);
	ArrayList<Bid>findByParentOrderByNum(Auction parent);

	@Query("select b from Bid b where b.parent.num = :parent order by b.price desc")
	ArrayList<Bid> findByBuyerByPrice(int parent);

	ArrayList<Bid> findByBuyerOrderByNumDesc(Member buyer);
}
