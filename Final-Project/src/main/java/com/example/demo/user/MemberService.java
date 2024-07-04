package com.example.demo.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class MemberService {
   @Autowired
   private MemberDao dao;
   
   @Autowired
   private PasswordEncoder passwordEncoder;

   // 추가
   public MemberDto save(MemberDto dto) {
      // dao.save() 반환값: 방금 추가/수정한 그 행을 검색해서 entity에 담아서 반환
      Member u = dao.save(new Member(dto.getId(),passwordEncoder.encode(dto.getPwd()),dto.getName(),dto.getEmail(),dto.getCardnum(),
            dto.getPoint(),dto.getRanks(),dto.getExp(),dto.getType()));
      return new MemberDto(u.getId(),u.getPwd(),u.getName(),u.getEmail(),u.getCardnum(),u.getPoint(),u.getRanks(),u.getExp(),u.getType());
   }

   public void edit(MemberDto dto) {
      Member u = dao.findById(dto.getId()).orElse(null);
      u.setName(dto.getName());
      u.setExp(dto.getExp());
      u.setType(dto.getType());
      u.setEmail(dto.getEmail());
      u.setCardnum(dto.getCardnum());
      u.setPoint(dto.getPoint());
      u.setRanks(dto.getRanks());
      dao.save(u);
   }

   // id로 검색
   public MemberDto getUser(String id) {
      // dao.findById(pk): pk기준으로 검색
      Member u = dao.findById(id).orElse(null);// orElse(null): 검색결과 없으면 널 반환
      if (u == null) {
         return null;
      }
      return new MemberDto(u.getId(),u.getPwd(),u.getName(),u.getEmail(),u.getCardnum(),u.getPoint(),u.getRanks(),u.getExp(),u.getType());
   }

   // 전체검색: findAll()
   public ArrayList<MemberDto> getAll() {
      List<Member> l = dao.findAll();
      ArrayList<MemberDto> list = new ArrayList<>();
      for (Member u : l) {
         list.add(new MemberDto(u.getId(),u.getPwd(),u.getName(),u.getEmail(),u.getCardnum(),u.getPoint(),u.getRanks(),u.getExp(),u.getType()));
      }
      return list;
   }
   
   //id기준 삭제
   public void delMember(String id) {
      dao.deleteById(id);
   }
   
   //type을 검색
   public ArrayList<MemberDto> getByType(String type) {
      List<Member> l = dao.findByType(type);
      ArrayList<MemberDto> list = new ArrayList<>();
      for (Member u : l) {
         list.add(new MemberDto(u.getId(),u.getPwd(),u.getName(),u.getEmail(),u.getCardnum(),u.getPoint(),u.getRanks(),u.getExp(),u.getType()));
      }
      return list;
   }
}