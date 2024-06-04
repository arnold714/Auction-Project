package com.example.demo.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor 
@AllArgsConstructor 
@ToString
public class CardDto {
	
	private int cardnum;
    private String expiryDate; //유효기한
    private int cvc;
    private int pwd; // 카드 비
    private int price;
}
