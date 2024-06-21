package com.example.demo.user;

import java.util.HashMap;
import java.util.Map;

import com.example.demo.card.Card;
import com.example.demo.card.CardDto;
import com.example.demo.card.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
public class MemberController {

	@Autowired
	private MemberService service;
	@Autowired
	private CardService cservice;

	@GetMapping("/join")
	public String joinForm() {
		return "member/login";
	}
	
	@PostMapping("/join")
	public String join(MemberDto u) {
		service.save(u);
		return "redirect:/";
	}
	
	@GetMapping("/loginform")
	public String loginForm(String path,ModelMap map,HttpSession session) {
		map.addAttribute("path",path);
		return "member/login";
	}

	@RequestMapping("/auth/login")
	public String alogin() {
		return "index";
	}

	// 관리자가 로그인 후 이동할 경로
	@RequestMapping("/auth/index_admin")
	public String adminHome() {
		return "index_admin";
	}

	// 회원이 로그인 후 이동할 경로
	@RequestMapping("/auth/index_member")
	public String memberHome() {
		return "index_member";
	}

	@RequestMapping("/auth/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	@RequestMapping("/auth/out")
	public String out(String id) {
		service.delMember(id);
		return "redirect:/logout";
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
			m.setRank("Diamond");
		}else if(m.getExp()>=400000){
			m.setRank("Gold");
		}else if(m.getExp()>=100000){
			m.setRank("Silver");
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
