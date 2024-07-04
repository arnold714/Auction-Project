package com.example.demo.auth;

import com.example.demo.user.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
public class TokenUserDetails implements UserDetails {

    /**
     * 인증에 사용될 정보 vo
     */
    private static final long serialVersionUID = 1L;
    private final Member m;//인증에 사용될 id/pwd/type 등의 정보를 갖는 entity를 멤버로 갖음
    public TokenUserDetails(Member m) {//생성자로 의존성 주입
        this.m = m;
    }

    //인증 객체의 권한 목록 저장
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        List<GrantedAuthority> list = new ArrayList<>();
        String role = "";
        if (m.getType().equals("admin")){
            role = "ROLE_ADMIN";
        }else if (m.getType().equals("seller")){
            role = "ROLE_SELLER";
        } else if (m.getType().equals("consumer")){
            role = "ROLE_CONSUMER";
        }
        boolean add = list.add(new SimpleGrantedAuthority(role));
        return list;
    }

    @Override
    public String getPassword() {//인증 객체의 pwd 반환
        // TODO Auto-generated method stub
        return m.getPwd();
    }

    @Override
    public String getUsername() {//인증 객체의 id 반환
        // TODO Auto-generated method stub
        return m.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }

}