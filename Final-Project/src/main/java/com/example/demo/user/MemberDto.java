package com.example.demo.user;

import com.example.demo.card.Card;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class MemberDto {
       private String id;

       private String pwd;
       private String name;
       private String email;

       private Card cardnum;

       private int point;
       private String ranks;
       private int exp;
       private String type;
       
       public static MemberDto create(Member u) {
       	return MemberDto.builder()
       			.id(u.getId())
       			.pwd(u.getPwd())
       			.name(u.getName())
       			.email(u.getEmail())
       			.cardnum(u.getCardnum())
       			.point(u.getPoint())
       			.rank(u.getRanks())
       			.exp(u.getExp())
       			.type(u.getType())
       			.build();
       }

    @Builder
   	public MemberDto(String id, String pwd, String name, String email, Card cardnum, int point, String rank, int exp,
					 String type) {
   		this.id = id;
   		this.pwd = pwd;
   		this.name = name;
   		this.email = email;
   		this.cardnum = cardnum;
   		this.point = point;
   		this.ranks = rank;
   		this.exp = exp;
   		this.type = type;
   	}
}