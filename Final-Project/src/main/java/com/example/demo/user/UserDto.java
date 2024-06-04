package com.example.demo.user;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.example.demo.card.Card;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class UserDto {
       private String id;

       private String pwd;
       private String name;
       private String email;

       private Card cardnum;

       private int point;
       private String rank;
       private int exp;
       private String type;
       
       public static UserDto create(Users u) {
       	return UserDto.builder()
       			.id(u.getId())
       			.pwd(u.getPwd())
       			.name(u.getName())
       			.email(u.getEmail())
       			.cardnum(u.getCardnum())
       			.point(u.getPoint())
       			.rank(u.getRank())
       			.exp(u.getExp())
       			.type(u.getType())
       			.build();
       }

    @Builder
   	public UserDto(String id, String pwd, String name, String email, Card cardnum, int point, String rank, int exp,
   			String type) {
   		this.id = id;
   		this.pwd = pwd;
   		this.name = name;
   		this.email = email;
   		this.cardnum = cardnum;
   		this.point = point;
   		this.rank = rank;
   		this.exp = exp;
   		this.type = type;
   	}
}