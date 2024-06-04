package com.example.demo.card;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/auth/card")
public class CardController {

	@Autowired
	private CardService service;
	
	@GetMapping
	public String listCards(Model model) {
		List<CardDto> cards = service.getByName("");
		model.addAttribute("cards", cards);
		return "cardlist";
	}
	
	@GetMapping("/{num}")
	public String getCard(@PathVariable int num, Model model) {
		CardDto card = service.getCard(num);
		model.addAttribute("card", card);
		return "carddetail";
	}
	
	@GetMapping("/add")
	public String showAddCardForm(Model model) {
		model.addAttribute("card", new CardDto());
		return "cardform";
	}
	
	@PostMapping("/add")
	public String addCard(@ModelAttribute CardDto cardDto) {
		service.saveCard(cardDto);
		return "redirect:/auth/card";
	}
	
	@PostMapping("/edit/{num}")
	public String editCard(@PathVariable int cardnum, @ModelAttribute CardDto cardDto) {
		cardDto.setCardnum(cardnum);
		service.saveCard(cardDto);
		return "redirect:/auth/card";
	}
	
	@GetMapping("/delete/{num}")
	public String deleteCard(@PathVariable int cardnum) {
		service.deleteCard(cardnum);
		return "redirect:/auth/card";
	}
}
