package com.example.demo.card;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/auth/card")
public class CardController {

	@Autowired
	private CardService service;
	
	@GetMapping("/list")
	public String list(ModelMap map) {
		map.addAttribute("list", service.getAllCards());
		return "card/list";
	}
	
	@GetMapping("/add")
	public void addform() {
	}
	
	@PostMapping("/add")
	public String add(CardDto c) {
		service.save(c);
		return "card/add";
	}
	
	@GetMapping("/get")
	public String getbycardnum(int cardnum, ModelMap map) {
		map.addAttribute("s", service.getCard(cardnum));
		return "card/detail";
	}
	
	
	
	@GetMapping("/del")
	public String del(int cardnum) {
		service.delCard(cardnum);
		return "redirect:/index";
	}
	
}
