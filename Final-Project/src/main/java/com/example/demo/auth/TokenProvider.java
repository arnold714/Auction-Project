package com.example.demo.auth;


import com.example.demo.user.MemberDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component // 토큰을 생성 / 토큰의 추가 정보를 추출 / 토큰의 유효성 검사
public class TokenProvider {
    private final long expiredTime = 1000 * 60 * 60 * 1L; // 유효시간 1시간
    private static final Key key = Keys.hmacShaKeyFor("asdfdfghjhgukjhljkdyhywsrgegsrthtyjguil;yujrthyjfhgkeargaegthaegahukgjhlsfghdghg"
            .getBytes(StandardCharsets.UTF_8));


    private final TokenMemUserDetailsService userDetailService;

    //토큰 생성하여 반환
    public String getToken(MemberDto dto) {
        Date now = new Date();
        //토큰에다가 인증요청한 사람의 정보를 넣음
        //Claims는 추가정보 헤더, 페이로드<-여기에 있음, 시그니쳐
        return Jwts.builder().setSubject(dto.getId()).setHeader(createHeader()).setClaims(createClaims(dto))
                .setExpiration(new Date(now.getTime() + expiredTime)).signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public Map<String, Object> createHeader() {
        Map<String, Object> map = new HashMap<>();
        map.put("typ", "JWT");
        map.put("alg", "HS256");
        map.put("regDate", System.currentTimeMillis());
        return map;
    }

    public Map<String, Object> createClaims(MemberDto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", dto.getId());
        map.put("roles", dto.getType());
        return map;
    }

    // 토큰을 파싱하여 클레임정보만 반환
    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // claims에서 username 추출
    public String getUserName(String token) {
        return (String) getClaims(token).get("username");
    }

    // claims에서 roles 추출
    public String getRoles(String token) {
        return (String) getClaims(token).get("roles");
    }

    // 요청 헤더에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("auth_token");
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    //토큰 인증
    public Authentication getAuthentication(String token) {
        //토큰에 저장된 username을 꺼내서 db 검색
        UserDetails user =  userDetailService.loadUserByUsername(getUserName(token));
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }
}