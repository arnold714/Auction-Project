package com.example.demo.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.auth.TokenMemUserDetailsService;
import com.example.demo.auth.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.example.demo.auction.Auction;
import com.example.demo.auction.AuctionDto;
import com.example.demo.auction.AuctionService;
import com.example.demo.card.Card;
import com.example.demo.card.CardDto;
import com.example.demo.card.CardService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class MemberController {

	@Autowired
	private MemberService service;
	@Autowired
	private TokenProvider provider;
	@Autowired
	private CardService cservice;
	@Autowired
	private AuctionService aservice;
	@Autowired
	private AuthenticationManagerBuilder abuilder;
	
	@PostMapping("/join")
	public Map join(MemberDto u) {
		Map<String, Object> model = new HashMap<>();
		try {
			service.save(u);
		} catch (Exception e) {
			e.printStackTrace();
			model.put("error", e.getMessage());
		}
		model.put("flag", true);

		return model;
	}
	
//	@GetMapping("/loginform")
//	public String loginForm(String path,ModelMap map,HttpSession session,String msg) {
//		map.addAttribute("path",path);
//		map.addAttribute("msg",msg);
//		return "member/login";
//	}
@PostMapping("/login")
public Map login(String id, String pwd) {
	log.info("id={}, pwd={}", id, pwd);
	UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(id, pwd);
	MemberDto memberDto = service.getUser(authtoken.getName());
	log.info("authtoken={}", authtoken);
	log.info("token={}", provider.getToken(service.getUser(id)));
	Authentication auth = abuilder.getObject().authenticate(authtoken);
	boolean flag = auth.isAuthenticated();
	System.out.println("인증결과:" + flag);
	Map map = new HashMap();
	if(flag) {
		String token = provider.getToken(service.getUser(id));
		map.put("token", token);
	}
	map.put("flag", flag);
	map.put("id",authtoken.getName());
	map.put("type", memberDto.getType());
	return map;
}
	@RequestMapping("/auth/login")
	public Map<String,Object> alogin(String id, String pwd) {
		Map<String,Object> map = new HashMap<>();
		UsernamePasswordAuthenticationToken auth_token = new UsernamePasswordAuthenticationToken(id, pwd);
		ArrayList<AuctionDto> l=aservice.getAllByBids("경매중");
		ArrayList<String> list= new ArrayList<>();
		for(int i=0;i<l.size();i++) {
			if(l.get(i).getType().equals(Auction.Type.BLIND)) {
				l.get(i).setMax(l.get(i).getMin());
			}
			list.add(null);
			map.put("HBA"+(list.size()),l.get(i));
			if(list.size()>5) {
				break;
			}
		}
		ArrayList<AuctionDto> l2=aservice.getAll();
		ArrayList<String> list2= new ArrayList<>();
		for(int i=0;i<l2.size();i++) {
			if(l2.get(i).getType().equals(Auction.Type.BLIND) && l2.get(i).getStatus().equals("경매중")) {
				list2.add(null);
				map.put("BA"+(list2.size()),l2.get(i));
			}
			if(list2.size()>5) {
				break;
			}
		}
		ArrayList<AuctionDto> l3=aservice.getAll();
		ArrayList<String> list3= new ArrayList<>();
		for(int i=0;i<l2.size();i++) {
			if(l2.get(i).getType().equals(Auction.Type.EVENT) && l3.get(i).getStatus().equals("경매중")) {
				list3.add(null);
				map.put("EA"+(list3.size()),l3.get(i));
			}
			if(list3.size()>5) {
				break;
			}
		}
		ArrayList<AuctionDto> l4 = aservice.getAll();
		ArrayList<String> list4= new ArrayList<>();
		for(int i=0;i<l4.size();i++) {
			if(l4.get(i).getStatus().equals("경매중")) {
				list4.add(null);
				map.put("LA"+(list4.size()),l4.get(i));
			}
			if(list4.size()>5) {
				break;
			}
		}
		return map;
	}

//	// 관리자가 로그인 후 이동할 경로
//	@RequestMapping("/auth/index_admin")
//	public String adminHome() {
//		return "index_admin";
//	}
//
//	// 회원이 로그인 후 이동할 경로
//	@RequestMapping("/auth/index_member")
//	public String memberHome() {
//		return "index_member";
//	}

	@RequestMapping("/auth/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	@RequestMapping("/auth/out")
	public String out(String id, ModelMap map) {
		service.delMember(id);
		map.addAttribute("list",service.getAll());
		return "member/list";
	}

	@RequestMapping("/auth/member/list")
	public String list(ModelMap map) {
		map.addAttribute("list",service.getAll());
		return "member/list";
	}

	@GetMapping("/auth/member/edit")
	public String editform(String id, ModelMap map) {
		MemberDto m = service.getUser(id);
		map.addAttribute("m", m);
		return "member/edit";
	}

	@PostMapping("/auth/member/edit")
	public String edit(MemberDto m) {
		service.edit(m);
		return "redirect:/auth/member/list";
	}

	@GetMapping("/auth/member/edit2")
	public ModelAndView editform2(String id) {
		MemberDto m = service.getUser(id);
		ModelAndView mav = new ModelAndView("member/edit");
		mav.addObject("m", m);
		return mav;
	}

	@PostMapping("/auth/member/edit2")
	public String edit2(MemberDto m) {
		MemberDto d = service.getUser(m.getId());
		d.setName(m.getName());
		d.setEmail(m.getEmail());
		if(!m.getPwd().isEmpty()) {
			service.save(d);
			return "/index_member";
		}
		service.edit(d);
		return "/index_member";
	}

	@GetMapping("/auth/member/card")
	public String cardform(String id, ModelMap map) {
		MemberDto m = service.getUser(id);
		if(m.getCardnum()!=null){
			map.addAttribute("flag",false);
		}
		else{
			map.addAttribute("flag",true);
		}
		map.addAttribute("member", m);
		return "member/card";
	}

	@PostMapping("/auth/member/card")
	public String card(CardDto dto, String id, ModelMap map) {
		//일치하는 카드 가져오기
		MemberDto m = service.getUser(id);
		CardDto c = cservice.get(Card.create(dto));
		if(c==null){
			map.addAttribute("msg","일치하는 카드가 없습니다");
			map.addAttribute("flag",true);
			map.addAttribute("member", m);
			return "member/card";
		}
		log.debug("c: {}", c);
		log.debug("m: {}", m);
		m.setCardnum(Card.create(c));
		//같은카드를 두명이서 등록하면 오류 발생
		try {
			service.edit(m);
		}catch(Exception e){
			map.addAttribute("msg","이미 등록된 카드입니다.");
			map.addAttribute("flag",true);
			map.addAttribute("member", m);
			return "member/card";
		}
		return "redirect:/auth/member/card?id="+id;
	}

	@GetMapping("/auth/member/point")
	public String pointform(String id, ModelMap map) {
		MemberDto m = service.getUser(id);
		map.addAttribute("member", m);
		if(m.getCardnum() == null) {
			map.addAttribute("flag",true);
			return "member/card";
		}
		return "member/point";
	}

	@PostMapping("/auth/member/point")
	public String point(String id, String point, String customPoint, ModelMap map) {
		MemberDto m = service.getUser(id);
		//point가 한글일때 숫자가 아닐때 오류처리
		if(point.equals("custom")){
			m.setPoint(m.getPoint() + Integer.parseInt(customPoint));
			m.setExp(m.getExp() + Integer.parseInt(customPoint));
		}else {
			m.setPoint(m.getPoint() + Integer.parseInt(point));
			m.setExp(m.getExp() + Integer.parseInt(point));
		}

		if(m.getExp()>=1400000){
			m.setRanks("Diamond");
		}else if(m.getExp()>=400000){
			m.setRanks("Gold");
		}else if(m.getExp()>=100000){
			m.setRanks("Silver");
		}
		service.edit(m);
		map.addAttribute("member", m);
		return "member/point";
	}

	@ResponseBody
	@GetMapping("/idcheck")
	public Map idcheck(String id) {
		Map map = new HashMap();
		MemberDto u = service.getUser(id);
		boolean flag = false;
		if (u == null) {
			flag = true;
		}
		map.put("flag", flag);
		return map;
	}
}
