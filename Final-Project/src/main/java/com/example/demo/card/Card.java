package com.example.demo.card;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Card { // 더미데이터
    @Id
    @SequenceGenerator(name = "seq_gen", sequenceName = "seq_card", allocationSize = 1) // 시퀀스 생성
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_card")
    private int cardnum;
    private String expiryDate; //유효기한
    private int cvc;
    private int pwd; // 카드 비번	
    private int price;

}
