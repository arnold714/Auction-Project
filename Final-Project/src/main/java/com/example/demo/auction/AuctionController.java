package com.example.demo.auction;

import java.util.Calendar;
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
import com.example.demo.user.MemberDto;
import com.example.demo.user.MemberService;

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
	@Autowired
	private MemberService mservice;
	
	@GetMapping("add")
	public String addform(int prodnum,ModelMap map) {
		ProductDto prod=pservice.getProd(prodnum);
		map.addAttribute("prod", prod);
		return "/auction/add";
	}
	@GetMapping("/event")
	public String eventform(String mino,ModelMap map) {
		System.out.println(mino);
		map.addAttribute("mino", mino);
		return "/auction/event";
	}
	
	@PostMapping("/add")
	public String add(AuctionDto a) {
		a.setMax(a.getMin());
		a.setStatus("경매중");
		if(a.getType().equals(Auction.Type.EVENT)) {
			a.setMax(0);
		}
		a.setStart_time(new Date());
		aservice.setTime(a, a.getTime());
		aservice.save(a);
		
		return "/index_member";
	}
	
	@MessageMapping("/price")
	@SendTo("/sub/bid")
	public Map send(BidAddDto b) throws InterruptedException {
		Map map=new HashMap();
		MemberDto buyer= mservice.getUser(b.getBuyer());
		AuctionDto auction=aservice.get(b.getParent());
		map.put("parent", b.getParent());
		BidDto dto=new BidDto(b.getNum(),Auction.create(auction),Member.create(buyer),b.getPrice(),new Date());
		if(dto.getBidtime().after(auction.getEnd_time())) {
			map.put("msg","end");
			return map;
		}
		if(!(auction.getType().equals(Auction.Type.EVENT))) {
			if(bservice.getByParent(b.getParent()).size()>0 && !(auction.getType().equals(Auction.Type.BLIND))) {
				BidDto pbid=bservice.getByBuyer(auction.getNum());
				int getPoint=pbid.getPrice();
				System.out.println(getPoint);
				MemberDto pbuyer= mservice.getUser(pbid.getBuyer().getId());
				System.out.println(pbuyer.getId());
				pbuyer.setPoint(pbuyer.getPoint()+getPoint);
				System.out.println(pbuyer.getPoint());
				mservice.edit(pbuyer);
			}
		}
		buyer.setPoint(buyer.getPoint()-b.getPrice());
		bservice.save(dto);
		auction.setBcnt(auction.getBcnt()+1);
		if((auction.getType().equals(Auction.Type.EVENT))) {
			System.out.println(b.getPrice());
			auction.setMax(auction.getMax()+b.getPrice());
		}else {
			auction.setMax(b.getPrice());
		}
		System.out.println(3);
		aservice.save(auction);
		mservice.edit(buyer);
		String price=""+b.getPrice();
		map.put("price", auction.getMax());
		return map;
	}
	
	@MessageMapping("/status")
	@SendTo("/sub/bid")
	public Map change(int parent) throws InterruptedException {
		Map map=new HashMap();
		AuctionDto auction=aservice.get(parent);
		map.put("parent", parent);
		if(auction.getEnd_time().before(new Date())) {
			auction.setStatus("경매 마감");
			aservice.save(auction);
			map.put("msg", "경매 마감");
		}
		return map;
	}
	
	
	@RequestMapping("/detail")
	public String detail(int num,ModelMap map,HttpSession session) {
		AuctionDto dto=aservice.get(num);
		map.addAttribute("s", aservice.get(num));
		Calendar cal = Calendar.getInstance();
		cal.setTime(dto.getStart_time());
		String time=""+cal.get(Calendar.DAY_OF_MONTH);
		time+="일 "+cal.get(Calendar.HOUR_OF_DAY);
		time+="시"+cal.get(Calendar.MINUTE)+"분";
		cal.setTime(dto.getEnd_time());
		String time2=""+cal.get(Calendar.DAY_OF_MONTH);
		time2+="일 "+cal.get(Calendar.HOUR_OF_DAY);
		time2+="시"+cal.get(Calendar.MINUTE)+"분";
		map.addAttribute("start_time", time);
		map.addAttribute("end_time", time2);
		String id=(String) session.getAttribute("loginId");
		map.addAttribute("point",mservice.getUser(id).getPoint());
		return "/auction/detail";
	}

	@GetMapping("/list")
	public String list(ModelMap map) {
		map.addAttribute("list", aservice.getAll());
		return "auction/adminlist";
	}

	@GetMapping("/myauction")
	public String myauction(ModelMap map,HttpSession session) {
		String seller = (String) session.getAttribute("loginId");
		map.addAttribute("list", aservice.getBySeller(seller));
		return "auction/myauction";
	}

	@GetMapping("/mybidauction")
	public String mybidauction(ModelMap map,HttpSession session) {
		String buyer = (String) session.getAttribute("loginId");
		map.addAttribute("list", bservice.getByBuyer2(buyer));
		return "auction/mybidauction";
	}

	@GetMapping("/stop")
	public String stop(int num){
		AuctionDto auction=aservice.get(num);
		auction.setStatus("경매 마감");
		return "redirect:/auth/report/list";
	}

	@RequestMapping("/del")
	public String del(int num, ModelMap map) {
		aservice.delete(num);
		map.addAttribute("list", aservice.getAll());
		return "auction/adminlist";
	}
	
	
}
