package com.example.demo.auction;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.bid.BidAddDto;
import com.example.demo.bid.BidDto;
import com.example.demo.bid.BidService;
import com.example.demo.product.ProductDto;
import com.example.demo.product.ProductService;
import com.example.demo.user.Member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/auth/auction")
@RequiredArgsConstructor
public class AuctionController {

	@Autowired
	private AuctionService aservice;
	@Autowired
	private BidService bservice; // 추가, 수정 / parent로 검색
	@Autowired
	private ProductService pservice;
	@GetMapping("add")
	public String addform(int prodnum,ModelMap map) {
		ProductDto prod=pservice.getProd(prodnum);
		map.addAttribute("prod", prod);
		return "/auction/add";
	}
	
	@PostMapping("/add")
	public String add(AuctionDto a) {
		a.setMax(a.getMin());
		a.setStatus("경매중");
		a.setStart_time(new Date());
		aservice.setTime(a, a.getTime());
		aservice.save(a);
		return "redirect:/index_member";
	}
	@GetMapping("/detail")
	public String detail(int num,ModelMap map) {
		map.addAttribute("s", aservice.get(num));
		return "/auction/detail";
	}
	
	@GetMapping("/list")
	public String list(ModelMap map) {
		map.addAttribute("list", aservice.getByStatus("경매중"));
		return "auction/list";
	}
	@GetMapping("/myauction")
	public String myauction(String seller,ModelMap map,HttpSession session) {
		map.addAttribute("list", aservice.getBySeller(seller));
		return "auction/myauction";
	}

	@MessageMapping("/price")
	@SendTo("/auth/auction/bid")
	public Map send(BidAddDto b) throws InterruptedException {
		System.out.println(b);
		try{
			bservice.save(new BidDto(b.getNum(),new Auction(b.getParent()),new Member(b.getBuyer()),b.getPrice()));
			AuctionDto d=aservice.get(b.getParent()); 
			d.setMax(b.getPrice());
			aservice.save(d);
			
		}catch(Exception e) {
			return null;
		}
		Map map=new HashMap();
		String price=""+b.getPrice();
		map.put("price", price);
		map.put("parent", b.getParent());
		return map;
	}
	
	
}